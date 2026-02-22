Write-Host "===== Appium Auto Setup Start =====" -ForegroundColor Cyan

# -----------------------------
# 1. Android SDK 경로 (수정 필요)
# -----------------------------
$ANDROID_SDK_PATH = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"

if (!(Test-Path $ANDROID_SDK_PATH)) {
    Write-Host "❌ Android SDK not found at $ANDROID_SDK_PATH" -ForegroundColor Red
    exit 1
}

# -----------------------------
# 2. 임시 환경변수 설정
# -----------------------------
$env:ANDROID_HOME = $ANDROID_SDK_PATH
$env:ANDROID_SDK_ROOT = $ANDROID_SDK_PATH
$env:JAVA_HOME = "C:\Users\$env:USERNAME\.jdks\corretto-21.0.10"; # 자신이 설치한 jdk 폴더로 지정

$env:PATH = "$ANDROID_SDK_PATH\platform-tools;" +
            "$ANDROID_SDK_PATH\emulator;" +
            "$env:JAVA_HOME\bin;" +
            $env:PATH

Write-Host "✅ Environment variables set (session only)"

# -----------------------------
# 3. Node LTS 설치 및 사용
# -----------------------------
Write-Host "Installing Node LTS..."
nvm install lts
nvm use lts

# -----------------------------
# 4. Appium 설치
# -----------------------------
Write-Host "Installing Appium..."
npm i -g appium

Write-Host "Installing Appium Drivers..."
appium driver install uiautomator2
appium driver install xcuitest
appium driver install espresso

# -----------------------------
# 5. 설치 확인
# -----------------------------
Write-Host ""
Write-Host "Node Version:"
node -v

Write-Host "Appium Version:"
appium -v

Write-Host ""
Write-Host "ADB Devices:"
adb devices

# -----------------------------
# 6. Appium 서버 실행
# -----------------------------
Write-Host ""
Write-Host "🚀 Starting Appium Server..." -ForegroundColor Green
appium