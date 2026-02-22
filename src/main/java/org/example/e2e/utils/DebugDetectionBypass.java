package org.example.e2e.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 안드로이드 앱의 디버깅 감지를 우회하기 위한 유틸리티 클래스
 * adb 명령어를 통해 시스템 속성 및 디버그 플래그를 조작합니다.
 */
public class DebugDetectionBypass {

  /**
   * 주어진 기기에서 디버그 감지를 우회하는 모든 설정을 적용합니다.
   * @param udid 기기의 UDID (선택사항, null이면 연결된 기기 사용)
   */
  public static void bypassDebugDetection(String udid) {
    try {
      // 1. ro.debuggable 속성 비활성화
      disableDebuggableProperty(udid);

      // 2. 개발자 옵션 비활성화
      disableDeveloperOptions(udid);

      // 3. USB 디버깅 비활성화
      disableUsbDebugging(udid);

      // 4. SELinux permissive 모드로 설정
      setSELinuxPermissive(udid);

      // 5. ro.secure 속성 활성화
      enableSecureProperty(udid);

      // 6. ro.kernel.android.checkjni 비활성화
      disableCheckJni(udid);

      System.out.println("✅ 모든 디버그 감지 우회 설정이 적용되었습니다.");
    } catch (Exception e) {
      System.err.println("❌ 디버그 감지 우회 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * ro.debuggable 속성을 비활성화합니다.
   */
  private static void disableDebuggableProperty(String udid) throws IOException {
    // ro.debuggable이 0이면 디버깅 비활성화
    executeAdbCommand(udid, "shell", "setprop", "ro.debuggable", "0");
    System.out.println("✓ ro.debuggable = 0 (비활성화)");
  }

  /**
   * 개발자 옵션을 비활성화합니다.
   */
  private static void disableDeveloperOptions(String udid) throws IOException {
    // 개발자 옵션 숨김 (ro.com.google.clientidbase 수정)
    executeAdbCommand(udid, "shell", "setprop", "ro.com.google.clientidbase", "android-google");
    System.out.println("✓ 개발자 옵션 비활성화");
  }

  /**
   * USB 디버깅을 비활성화합니다.
   */
  private static void disableUsbDebugging(String udid) throws IOException {
    // Settings 데이터베이스에서 adb 활성화 플래그 제거
    executeAdbCommand(udid, "shell", "settings", "put", "global", "adb_enabled", "0");
    System.out.println("✓ USB 디버깅 비활성화");
  }

  /**
   * SELinux를 permissive 모드로 설정합니다.
   */
  private static void setSELinuxPermissive(String udid) {
    try {
      executeAdbCommand(udid, "shell", "setenforce", "0");
      System.out.println("✓ SELinux permissive 모드 적용");
    } catch (Exception e) {
      System.out.println("⚠️  SELinux 설정 실패 (무시됨): " + e.getMessage());
    }
  }

  /**
   * ro.secure 속성을 활성화합니다.
   */
  private static void enableSecureProperty(String udid) throws IOException {
    executeAdbCommand(udid, "shell", "setprop", "ro.secure", "1");
    System.out.println("✓ ro.secure = 1 (활성화)");
  }

  /**
   * ro.kernel.android.checkjni을 비활성화합니다.
   */
  private static void disableCheckJni(String udid) throws IOException {
    executeAdbCommand(udid, "shell", "setprop", "ro.kernel.android.checkjni", "0");
    System.out.println("✓ ro.kernel.android.checkjni = 0 (비활성화)");
  }

  /**
   * adb 명령어를 실행합니다.
   */
  private static String executeAdbCommand(String udid, String... args) throws IOException {
    List<String> command = new ArrayList<>();
    command.add("adb");

    if (udid != null && !udid.isBlank()) {
      command.add("-s");
      command.add(udid);
    }

    command.addAll(Arrays.asList(args));

    ProcessBuilder pb = new ProcessBuilder(command);
    Process process = pb.start();

    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    }

    try {
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
          String line;
          while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
          }
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("adb 명령어 실행 중 중단됨", e);
    }

    return output.toString();
  }

  /**
   * 현재 기기의 디버그 상태를 확인합니다.
   */
  public static void checkDebugStatus(String udid) {
    try {
      System.out.println("\n=== 디버그 상태 확인 ===");

      String debuggable = executeAdbCommand(udid, "shell", "getprop", "ro.debuggable");
      System.out.println("ro.debuggable: " + debuggable.trim());

      String secure = executeAdbCommand(udid, "shell", "getprop", "ro.secure");
      System.out.println("ro.secure: " + secure.trim());

      String checkJni = executeAdbCommand(udid, "shell", "getprop", "ro.kernel.android.checkjni");
      System.out.println("ro.kernel.android.checkjni: " + checkJni.trim());

      String adbEnabled = executeAdbCommand(udid, "shell", "settings", "get", "global", "adb_enabled");
      System.out.println("adb_enabled: " + adbEnabled.trim());

    } catch (IOException e) {
      System.err.println("디버그 상태 확인 중 오류 발생: " + e.getMessage());
    }
  }

  /**
   * 에뮬레이터 감지를 우회하기 위해 빌드 속성을 변경합니다.
   */
  public static void bypassEmulatorDetection(String udid) {
    try {
      System.out.println("\n=== 에뮬레이터 감지 우회 설정 ===");

      // ro.kernel.qemu를 0으로 설정하여 에뮬레이터임을 숨김
      executeAdbCommand(udid, "shell", "setprop", "ro.kernel.qemu", "0");
      System.out.println("✓ ro.kernel.qemu = 0");

      // ro.hardware를 실제 기기처럼 변경
      executeAdbCommand(udid, "shell", "setprop", "ro.hardware", "goldfish");
      System.out.println("✓ ro.hardware 설정");

    } catch (IOException e) {
      System.err.println("⚠️  에뮬레이터 감지 우회 중 오류: " + e.getMessage());
    }
  }

  /**
   * frida 또는 유사한 동적 분석 도구 감지를 우회합니다.
   */
  public static void bypassDynamicAnalysisDetection(String udid) {
    try {
      System.out.println("\n=== 동적 분석 도구 감지 우회 ===");

      // ptrace 접근 차단 비활성화
      executeAdbCommand(udid, "shell", "setprop", "ro.debuggable", "0");
      System.out.println("✓ ptrace 감지 우회");

      // SELinux 정책 완화
      executeAdbCommand(udid, "shell", "setenforce", "0");
      System.out.println("✓ SELinux 완화");

    } catch (IOException e) {
      System.err.println("⚠️  동적 분석 감지 우회 중 오류: " + e.getMessage());
    }
  }
}

