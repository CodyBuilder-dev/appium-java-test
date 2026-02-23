@echo off
setlocal

set "SCRIPT=%~dp0start-appium2.ps1"

REM 옵션 지정하고 싶으면 아래처럼:
REM set "AVD_NAME=Pixel_7_API_34"
REM set "APPIUM_PORT=4723"

powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" ^
  -AVD_NAME "%AVD_NAME%"

pause