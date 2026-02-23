#Requires -Version 5.1
param(
    [string]$AVD_NAME = $env:AVD_NAME,
    [int]$APPIUM_PORT = 4723
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Step($msg) { Write-Host "`n== $msg ==" -ForegroundColor Cyan }
function Write-Ok($msg)   { Write-Host "✅ $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "⚠️  $msg" -ForegroundColor Yellow }
function Write-Err($msg)  { Write-Host "❌ $msg" -ForegroundColor Red }

function Has-Command($name) {
    return [bool](Get-Command $name -ErrorAction SilentlyContinue)
}

function Refresh-EnvFromRegistry {
    $machinePath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $userPath    = [Environment]::GetEnvironmentVariable("Path", "User")
    $env:Path = ($machinePath + ";" + $userPath)

    $env:NVM_HOME    = [Environment]::GetEnvironmentVariable("NVM_HOME", "Machine")
    if (-not $env:NVM_HOME) { $env:NVM_HOME = [Environment]::GetEnvironmentVariable("NVM_HOME", "User") }

    $env:NVM_SYMLINK = [Environment]::GetEnvironmentVariable("NVM_SYMLINK", "Machine")
    if (-not $env:NVM_SYMLINK) { $env:NVM_SYMLINK = [Environment]::GetEnvironmentVariable("NVM_SYMLINK", "User") }

    # 일반적인 nvm 설치 경로도 확인
    $commonNvmPaths = @(
        "$env:APPDATA\nvm",
        "$env:ProgramFiles\nvm"
    )
    foreach ($nvmPath in $commonNvmPaths) {
        if ((Test-Path $nvmPath) -and -not $env:NVM_HOME) {
            $env:NVM_HOME = $nvmPath
        }
    }

    # NVM_HOME을 PATH에 추가 (nvm 명령어를 위해)
    if ($env:NVM_HOME -and (Test-Path $env:NVM_HOME)) {
        if ($env:Path -notmatch [regex]::Escape($env:NVM_HOME)) {
            $env:Path = "$env:NVM_HOME;$env:Path"
        }
    }

    # NVM_SYMLINK를 PATH에 추가 (node/npm 명령어를 위해)
    if (-not $env:NVM_SYMLINK -and $env:NVM_HOME) {
        $symlinkPath = Join-Path (Split-Path $env:NVM_HOME -Parent) "nodejs"
        if (Test-Path $symlinkPath) {
            $env:NVM_SYMLINK = $symlinkPath
        }
    }

    if ($env:NVM_SYMLINK -and (Test-Path $env:NVM_SYMLINK)) {
        if ($env:Path -notmatch [regex]::Escape($env:NVM_SYMLINK)) {
            $env:Path = "$env:NVM_SYMLINK;$env:Path"
        }
    }
}

function Ensure-Nvm {
    # 먼저 환경 변수를 새로고침해서 nvm 경로 확인
    Refresh-EnvFromRegistry

    if (Has-Command "nvm") {
        Write-Ok "nvm already installed: $(nvm version)"
        return
    }

    Write-Step "nvm not found → installing"

    if (Has-Command "winget") {
        Write-Host "Using winget: winget install -e --id CoreyButler.NVMforWindows"
        winget install -e --id CoreyButler.NVMforWindows --accept-package-agreements --accept-source-agreements
    } else {
        Write-Warn "winget not found → fallback to GitHub latest release download"
        $api = "https://api.github.com/repos/coreybutler/nvm-windows/releases/latest"
        $release = Invoke-RestMethod -Uri $api -Headers @{ "User-Agent" = "PowerShell" }
        $asset = $release.assets | Where-Object { $_.name -match '^nvm-setup\.exe$' } | Select-Object -First 1
        if (-not $asset) { throw "Could not find nvm-setup.exe in latest release assets." }

        $tmp = Join-Path $env:TEMP "nvm-setup.exe"
        Write-Host "Downloading: $($asset.browser_download_url)"
        Invoke-WebRequest -Uri $asset.browser_download_url -OutFile $tmp

        Write-Host "Running installer (silent attempt)..."
        $p = Start-Process -FilePath $tmp -ArgumentList "/SP- /VERYSILENT /SUPPRESSMSGBOXES /NORESTART" -Wait -PassThru
        if ($p.ExitCode -ne 0) {
            Write-Warn "Silent install exit code=$($p.ExitCode). Trying normal install UI..."
            Start-Process -FilePath $tmp -Wait
        }
    }

    Refresh-EnvFromRegistry

    if (-not (Has-Command "nvm")) {
        throw "nvm install seems completed but 'nvm' still not found in this session. Reopen PowerShell or verify PATH."
    }
    Write-Ok "nvm installed: $(nvm version)"
}

function Ensure-NodeLts {
    Write-Step "Ensuring Node/NPM"

    # PATH 새로고침 (NVM_SYMLINK 포함)
    Refresh-EnvFromRegistry

    if (Has-Command "node" -and Has-Command "npm") {
        try {
            Write-Ok "node already available: $(node -v)"
            Write-Ok "npm already available:  $(npm -v)"
            Write-Ok "Skip Node LTS install (already installed)."
            return
        } catch { }
    }

    Write-Host "node/npm not found → nvm install/use lts"
    nvm install lts
    nvm use lts

    # node/npm PATH 반영을 위해 다시 새로고침
    Refresh-EnvFromRegistry

    if (-not (Has-Command "node") -or -not (Has-Command "npm")) {
        throw "node/npm still not found after nvm use lts. Check NVM_SYMLINK is in PATH."
    }
    Write-Ok "node ready: $(node -v)"
    Write-Ok "npm ready:  $(npm -v)"
}

function Ensure-Appium {
    Write-Step "Ensuring Appium"

    if (Has-Command "appium") {
        Write-Ok "Appium already installed: $(appium -v)"
        return
    }

    npm i -g appium

    if (-not (Has-Command "appium")) {
        throw "Appium install finished but 'appium' not found in PATH. Try reopening PowerShell."
    }
    Write-Ok "Appium installed: $(appium -v)"
}

function Get-InstalledDrivers {
    try { return (appium driver list --installed 2>$null) } catch { return "" }
}

function Ensure-AppiumDriver($driverName) {
    $installedText = Get-InstalledDrivers
    if ($installedText -match "(?im)^\s*${driverName}\b") {
        Write-Ok "Driver already installed: $driverName"
        return
    }
    Write-Host "Installing driver: $driverName"
    appium driver install $driverName
    Write-Ok "Driver installed: $driverName"
}

function Detect-AndroidSdkPath {
    # 1) 이미 세션에 있으면 우선
    if ($env:ANDROID_HOME -and (Test-Path $env:ANDROID_HOME)) { return $env:ANDROID_HOME }
    if ($env:ANDROID_SDK_ROOT -and (Test-Path $env:ANDROID_SDK_ROOT)) { return $env:ANDROID_SDK_ROOT }

    # 2) 일반적인 기본 경로들
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "$env:USERPROFILE\AppData\Local\Android\Sdk",
        "C:\Android\Sdk"
    )

    foreach ($p in $candidates) {
        if (Test-Path $p) { return $p }
    }

    return ""
}

function Ensure-AndroidEnv {
    Write-Step "Ensuring Android SDK + session env"

    $sdk = Detect-AndroidSdkPath
    if (-not $sdk) {
        throw "Android SDK path not found. Install Android Studio SDK or set ANDROID_HOME/ANDROID_SDK_ROOT before running."
    }

    $env:ANDROID_HOME = $sdk
    $env:ANDROID_SDK_ROOT = $sdk

    # JAVA_HOME: Android Studio 번들 JBR가 있으면 사용 (없으면 그냥 안 건드림)
    $jbrCandidates = @(
        "$env:ProgramFiles\Android\Android Studio\jbr",
        "$env:ProgramFiles\Android\Android Studio\jre"
    )
    foreach ($j in $jbrCandidates) {
        if (Test-Path $j) {
            $env:JAVA_HOME = $j
            break
        }
    }

    # PATH에 필요한 도구들 추가 (세션 한정)
    $toAdd = @(
        "$sdk\platform-tools",
        "$sdk\emulator",
        "$sdk\cmdline-tools\latest\bin",
        "$sdk\tools\bin"
    ) | Where-Object { Test-Path $_ }

    foreach ($p in $toAdd) {
        if ($env:Path -notmatch [regex]::Escape($p)) {
            $env:Path = "$p;$env:Path"
        }
    }

    if (-not (Has-Command "adb")) {
        throw "adb not found even after setting PATH. Check SDK install: $sdk\platform-tools"
    }

    Write-Ok "Android SDK: $sdk"
    Write-Ok "adb: $(adb version | Select-Object -First 1)"
    if ($env:JAVA_HOME) { Write-Ok "JAVA_HOME (session): $env:JAVA_HOME" } else { Write-Warn "JAVA_HOME not set (Android Studio JBR not found). Usually OK." }
}

function Reset-AdbServer {
    Write-Step "Resetting adb server"
    try { adb kill-server | Out-Null } catch { }
    adb start-server | Out-Null
    Write-Ok "adb server restarted"
}

function Get-AdbOnlineDevices {
    # "device" 상태만 필터
    $lines = adb devices | Select-Object -Skip 1
    $devices = @()
    foreach ($line in $lines) {
        if (-not $line) { continue }
        # e.g. emulator-5554 device
        $parts = $line -split "\s+"
        if ($parts.Length -ge 2 -and $parts[1] -eq "device") {
            $devices += $parts[0]
        }
    }
    return ,$devices
}

function Get-AvdList {
    if (-not (Has-Command "emulator")) { return @() }
    try {
        $out = emulator -list-avds
        if (-not $out) { return @() }
        return ($out -split "`r?`n" | Where-Object { $_ -and $_.Trim().Length -gt 0 })
    } catch {
        return @()
    }
}

function Start-EmulatorIfNeeded {
    Write-Step "Ensuring device/emulator is online"

    Reset-AdbServer

    $devices = @(Get-AdbOnlineDevices)
    if ($devices.Count -gt 0) {
        Write-Ok "Online device found: $($devices -join ', ')"
        return
    }

    Write-Warn "No online devices detected."

    $avds = Get-AvdList
    if ($avds.Count -eq 0) {
        Write-Warn "No AVD found (or emulator command unavailable)."
        Write-Host "Next steps:"
        Write-Host "  1) Create an AVD in Android Studio > Device Manager"
        Write-Host "  2) Or connect a real device with USB debugging"
        throw "Cannot proceed without a device."
    }

    if (-not $AVD_NAME) {
        $AVD_NAME = $avds[0]
        Write-Warn "AVD_NAME not provided → using first AVD: $AVD_NAME"
    } elseif ($avds -notcontains $AVD_NAME) {
        Write-Warn "AVD_NAME '$AVD_NAME' not found. Available: $($avds -join ', ')"
        $AVD_NAME = $avds[0]
        Write-Warn "Fallback to first AVD: $AVD_NAME"
    }

    Write-Step "Starting emulator: $AVD_NAME"
    # -no-snapshot-save 는 세션 꼬임 방지에 도움될 때가 많음
    Start-Process -FilePath "emulator" -ArgumentList "-avd `"$AVD_NAME`" -no-snapshot-save" | Out-Null
    Write-Ok "Emulator process started"

    # 기기 온라인 될 때까지 대기 (최대 120초)
    $deadline = (Get-Date).AddSeconds(120)
    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 3
        $devices = @(Get-AdbOnlineDevices)
        if ($devices.Count -gt 0) {
            Write-Ok "Online device found: $($devices -join ', ')"
            return
        }
    }

    throw "Emulator did not become online within 120 seconds. Check emulator window/AVD config."
}

function Start-AppiumServer {
    Write-Step "Starting Appium Server"
    Write-Host "Appium will run on port $APPIUM_PORT"
    appium -p $APPIUM_PORT
}

# -------------------------
# Main
# -------------------------
Write-Step "Pre-check"
Write-Host "PowerShell: $($PSVersionTable.PSVersion)"
Write-Host "User: $env:USERNAME"

Ensure-Nvm
Ensure-NodeLts
Ensure-Appium

Write-Step "Ensuring Appium Drivers"
Ensure-AppiumDriver "uiautomator2"
Ensure-AppiumDriver "xcuitest"
Ensure-AppiumDriver "espresso"

Ensure-AndroidEnv
Start-EmulatorIfNeeded

Write-Step "Sanity"
Write-Ok "adb devices:"
adb devices

Start-AppiumServer