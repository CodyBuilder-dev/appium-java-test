package org.example.e2e.driver;

import org.example.e2e.config.TestConfig;
import org.example.e2e.config.BypassConfig;
import org.example.e2e.utils.DebugDetectionBypass;
import org.example.e2e.utils.AdvancedAntiDebugBypass;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {

  public static AndroidDriver createAndroidDriver() {
    String appPath = TestConfig.appPath();
    if (appPath == null || appPath.isBlank()) {
      throw new IllegalArgumentException("APP_PATH system property is required. ex) -DAPP_PATH=C:\\apk\\myapp.apk");
    }

    UiAutomator2Options options = new UiAutomator2Options()
        .setPlatformName(TestConfig.platformName())
        .setDeviceName(TestConfig.deviceName())
        .setApp(appPath)
        .setNewCommandTimeout(Duration.ofSeconds(TestConfig.newCommandTimeoutSec()));

    // 실기기/특정 에뮬레이터 고정이 필요할 때
    if (!TestConfig.udid().isBlank()) {
      options.setUdid(TestConfig.udid());
    }

    // 선택: APK 실행 안정성을 높이고 싶으면 명시
    if (!TestConfig.appPackage().isBlank()) {
      options.setAppPackage(TestConfig.appPackage());
    }
    if (!TestConfig.appActivity().isBlank()) {
      options.setAppActivity(TestConfig.appActivity());
    }

    // ===== 디버깅 감지 우회 설정 =====
    // 1. NoSign: 앱 서명 검증 비활성화
    options.setNoSign(true);

    // 2. grantPermissions: 자동 권한 부여
    options.setAutoGrantPermissions(true);

    // 3. 에뮬레이터 감지 회피: avdLaunchTimeout 설정
    options.setAvdLaunchTimeout(Duration.ofSeconds(180));


    try {
      AndroidDriver driver = new AndroidDriver(new URL(TestConfig.appiumServerUrl()), options);

      // 디버그 감지 우회 설정 적용
      String udid = TestConfig.udid();
      if (!udid.isBlank()) {
        if (BypassConfig.isBasicBypassEnabled()) {
          DebugDetectionBypass.bypassDebugDetection(udid);
          if (BypassConfig.isDebugLoggingEnabled()) {
            DebugDetectionBypass.checkDebugStatus(udid);
          }
        }

        if (BypassConfig.isEmulatorBypassEnabled()) {
          DebugDetectionBypass.bypassEmulatorDetection(udid);
        }

        if (BypassConfig.isAdvancedBypassEnabled()) {
          AdvancedAntiDebugBypass.applyAllBypassMethods(udid);
        }

        if (BypassConfig.isFridaBypassEnabled()) {
          // Frida 감지는 기본 디버그 감지 우회에 포함됨
          AdvancedAntiDebugBypass.bypassFridaDetection(udid);
        }

        if (BypassConfig.isRootBypassEnabled()) {
          AdvancedAntiDebugBypass.bypassRootDetection(udid);
        }

        if (BypassConfig.isIntegrityBypassEnabled()) {
          AdvancedAntiDebugBypass.bypassAppIntegrityDetection(udid);
        }

        if (BypassConfig.isDumpPropertiesEnabled()) {
          AdvancedAntiDebugBypass.dumpSystemProperties(udid);
        }
      }

      return driver;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create AndroidDriver", e);
    }
  }
}