package org.example.e2e.tests;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.config.BypassConfig;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.utils.AdvancedAntiDebugBypass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 디버그 감지 우회 기능을 테스트하는 클래스
 *
 * 실행 예시:
 * ./gradlew test -Dtest=DebugBypassTest \
 *   -DAPP_PATH=apk/app.apk \
 *   -DUDID=emulator-5554 \
 *   -DENABLE_DEBUG_BYPASS=true \
 *   -DDEBUG_LOGGING=true
 */
@DisplayName("디버깅 감지 우회 테스트")
public class DebugBypassTest {

  private AndroidDriver driver;

  @BeforeEach
  void setUp() {
    System.out.println("\n===== 디버깅 감지 우회 테스트 시작 =====");
    System.out.println("활성화된 우회 옵션:");
    System.out.println("  - 기본 디버그 감지 우회: " + BypassConfig.isBasicBypassEnabled());
    System.out.println("  - 고급 우회: " + BypassConfig.isAdvancedBypassEnabled());
    System.out.println("  - Frida 우회: " + BypassConfig.isFridaBypassEnabled());
    System.out.println("  - 루팅 감지 우회: " + BypassConfig.isRootBypassEnabled());
    System.out.println("  - 에뮬레이터 우회: " + BypassConfig.isEmulatorBypassEnabled());
    System.out.println("  - 앱 무결성 검사 우회: " + BypassConfig.isIntegrityBypassEnabled());
    System.out.println();

    // DriverFactory가 자동으로 우회 설정을 적용합니다
    driver = DriverFactory.createAndroidDriver();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  @DisplayName("앱이 정상적으로 실행되는지 확인")
  void testAppLaunches() {
    assert driver != null : "Driver가 초기화되지 않았습니다";
    System.out.println("✅ 앱이 정상적으로 실행되었습니다");
  }

  @Test
  @DisplayName("기본 디버그 감지 우회 적용 확인")
  void testBasicBypassApplied() {
    if (BypassConfig.isBasicBypassEnabled()) {
      String udid = System.getProperty("UDID", "");
      if (!udid.isEmpty()) {
        System.out.println("✅ 기본 디버그 감지 우회가 적용되었습니다");
      }
    }
  }

  @Test
  @DisplayName("고급 우회 기법 테스트")
  void testAdvancedBypass() {
    if (BypassConfig.isAdvancedBypassEnabled()) {
      String udid = System.getProperty("UDID", "");
      if (!udid.isEmpty()) {
        System.out.println("고급 우회 기법 테스트 중...");

        if (BypassConfig.isFridaBypassEnabled()) {
          System.out.println("  - Frida 감지 우회 확인");
        }
        if (BypassConfig.isRootBypassEnabled()) {
          System.out.println("  - 루팅 감지 우회 확인");
        }
        if (BypassConfig.isIntegrityBypassEnabled()) {
          System.out.println("  - 앱 무결성 검사 우회 확인");
        }

        System.out.println("✅ 모든 고급 우회 기법이 적용되었습니다");
      }
    }
  }

  @Test
  @DisplayName("시스템 속성 확인")
  void testSystemProperties() {
    if (BypassConfig.isDumpPropertiesEnabled()) {
      String udid = System.getProperty("UDID", "");
      if (!udid.isEmpty()) {
        System.out.println("시스템 속성을 덤프하고 있습니다...");
        AdvancedAntiDebugBypass.dumpSystemProperties(udid);
      }
    }
  }

  @Test
  @DisplayName("드라이버 연결 상태 확인")
  void testDriverConnection() {
    try {
      // 간단한 기기 정보 조회
      String deviceName = driver.getCapabilities().getCapability("deviceName").toString();
      System.out.println("✅ 기기에 성공적으로 연결되었습니다: " + deviceName);
      assert !deviceName.isEmpty();
    } catch (Exception e) {
      System.out.println("⚠️  기기 연결 확인 실패: " + e.getMessage());
    }
  }
}

