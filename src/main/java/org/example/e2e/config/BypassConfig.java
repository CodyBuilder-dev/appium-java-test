package org.example.e2e.config;

/**
 * 디버깅 감지 우회 설정을 제어하는 설정 클래스
 */
public class BypassConfig {

  /**
   * 기본 디버그 감지 우회 활성화 여부
   */
  public static boolean isBasicBypassEnabled() {
    return System.getProperty("ENABLE_DEBUG_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * 고급 디버그 감지 우회 활성화 여부
   */
  public static boolean isAdvancedBypassEnabled() {
    return System.getProperty("ENABLE_ADVANCED_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * Frida 감지 우회 활성화 여부
   */
  public static boolean isFridaBypassEnabled() {
    return System.getProperty("ENABLE_FRIDA_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * 루팅 감지 우회 활성화 여부
   */
  public static boolean isRootBypassEnabled() {
    return System.getProperty("ENABLE_ROOT_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * 에뮬레이터 감지 우회 활성화 여부
   */
  public static boolean isEmulatorBypassEnabled() {
    return System.getProperty("ENABLE_EMULATOR_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * 앱 무결성 검사 우회 활성화 여부
   */
  public static boolean isIntegrityBypassEnabled() {
    return System.getProperty("ENABLE_INTEGRITY_BYPASS", "true").equalsIgnoreCase("true");
  }

  /**
   * 시스템 속성 덤프 활성화 여부 (디버깅 목적)
   */
  public static boolean isDumpPropertiesEnabled() {
    return System.getProperty("DUMP_SYSTEM_PROPERTIES", "false").equalsIgnoreCase("true");
  }

  /**
   * 디버그 상태 로깅 활성화 여부
   */
  public static boolean isDebugLoggingEnabled() {
    return System.getProperty("DEBUG_LOGGING", "true").equalsIgnoreCase("true");
  }
}

