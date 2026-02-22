package org.example.e2e.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 고급 디버깅 감지 우회 기법을 제공하는 유틸리티 클래스
 * Frida, Xposed, 루팅 감지 등을 우회합니다.
 */
public class AdvancedAntiDebugBypass {

  /**
   * 모든 고급 우회 기법을 적용합니다.
   */
  public static void applyAllBypassMethods(String udid) {
    System.out.println("\n===== 고급 디버깅 감지 우회 시작 =====");

    bypassRootDetection(udid);
    bypassFridaDetection(udid);
    bypassXposedDetection(udid);
    bypassAppIntegrityDetection(udid);
    disableAppMonitoring(udid);

    System.out.println("✅ 모든 고급 우회 설정이 적용되었습니다.\n");
  }

  /**
   * 루팅 감지를 우회합니다.
   */
  public static void bypassRootDetection(String udid) {
    System.out.println("\n--- 루팅 감지 우회 ---");
    try {
      // build.fingerprint와 build.tags 수정하여 정식 기기로 위장
      executeAdbCommand(udid, "shell", "setprop", "ro.build.tags", "release-keys");
      System.out.println("✓ build.tags = release-keys");

      // ro.secure를 1로 설정 (정식 빌드 표시)
      executeAdbCommand(udid, "shell", "setprop", "ro.secure", "1");
      System.out.println("✓ ro.secure = 1");

      // Magisk 감지 우회: build.selinux 설정
      executeAdbCommand(udid, "shell", "setprop", "ro.build.selinux", "1");
      System.out.println("✓ ro.build.selinux = 1");

    } catch (IOException e) {
      System.out.println("⚠️  루팅 감지 우회 중 오류: " + e.getMessage());
    }
  }

  /**
   * Frida 동적 계측 도구 감지를 우회합니다.
   */
  public static void bypassFridaDetection(String udid) {
    System.out.println("\n--- Frida 감지 우회 ---");
    try {
      // ptrace() 접근 제한 비활성화
      executeAdbCommand(udid, "shell", "setprop", "ro.debuggable", "0");
      System.out.println("✓ ro.debuggable = 0 (ptrace 차단)");

      // SELinux permissive 모드로 설정하여 ptrace 접근 허용
      executeAdbCommand(udid, "shell", "setenforce", "0");
      System.out.println("✓ SELinux = permissive");

      // 프로세스 모니터링 비활성화
      executeAdbCommand(udid, "shell", "setprop", "ro.kernel.android.checkjni", "0");
      System.out.println("✓ ro.kernel.android.checkjni = 0");

    } catch (IOException e) {
      System.out.println("⚠️  Frida 감지 우회 중 오류: " + e.getMessage());
    }
  }

  /**
   * Xposed 프레임워크 감지를 우회합니다.
   */
  private static void bypassXposedDetection(String udid) {
    System.out.println("\n--- Xposed 감지 우회 ---");
    try {
      // Xposed 모듈 파일 경로 숨김
      executeAdbCommand(udid, "shell", "setprop", "persist.sys.usb.config", "mtp");
      System.out.println("✓ USB 구성 정상화");

      // ro.boot.hardware 속성 설정
      executeAdbCommand(udid, "shell", "setprop", "ro.boot.hardware", "qemu");
      System.out.println("✓ ro.boot.hardware = qemu");

    } catch (IOException e) {
      System.out.println("⚠️  Xposed 감지 우회 중 오류: " + e.getMessage());
    }
  }

  /**
   * 앱 무결성 검사(SafetyNet, Play Integrity) 감지를 우회합니다.
   */
  public static void bypassAppIntegrityDetection(String udid) {
    System.out.println("\n--- 앱 무결성 검사 우회 ---");
    try {
      // build.fingerprint를 정상적인 값으로 설정
      String deviceBuild = "google/taimen/taimen:11/RP1A.200720.011/6827899:user/release-keys";
      executeAdbCommand(udid, "shell", "setprop", "ro.build.fingerprint", deviceBuild);
      System.out.println("✓ ro.build.fingerprint 설정");

      // ro.build.version.release 설정
      executeAdbCommand(udid, "shell", "setprop", "ro.build.version.release", "11");
      System.out.println("✓ ro.build.version.release = 11");

      // ro.product.model 설정
      executeAdbCommand(udid, "shell", "setprop", "ro.product.model", "Pixel 2");
      System.out.println("✓ ro.product.model = Pixel 2");

      // ro.product.manufacturer 설정
      executeAdbCommand(udid, "shell", "setprop", "ro.product.manufacturer", "Google");
      System.out.println("✓ ro.product.manufacturer = Google");

    } catch (IOException e) {
      System.out.println("⚠️  앱 무결성 검사 우회 중 오류: " + e.getMessage());
    }
  }

  /**
   * 앱 모니터링 및 프로파일링 기능을 비활성화합니다.
   */
  private static void disableAppMonitoring(String udid) {
    System.out.println("\n--- 앱 모니터링 비활성화 ---");
    try {
      // 프로파일링 비활성화
      executeAdbCommand(udid, "shell", "setprop", "debug.atrace.tags.enableflags", "0");
      System.out.println("✓ atrace 비활성화");

      // 프로세스 통계 비활성화
      executeAdbCommand(udid, "shell", "settings", "put", "global", "debug_view_attributes", "0");
      System.out.println("✓ 뷰 속성 디버깅 비활성화");

      // 시스템 앱 감시 비활성화
      executeAdbCommand(udid, "shell", "settings", "put", "global", "show_processes", "0");
      System.out.println("✓ 프로세스 표시 비활성화");

    } catch (IOException e) {
      System.out.println("⚠️  앱 모니터링 비활성화 중 오류: " + e.getMessage());
    }
  }

  /**
   * 앱 실행 중 디버거 프로세스 종료
   */
  public static void killDebuggerProcesses(String udid) {
    System.out.println("\n--- 디버거 프로세스 종료 ---");
    try {
      // adb 데몬 프로세스 확인 및 차단
      String[] debugProcesses = {
        "com.android.debugtools",
        "com.android.test",
        "frida-server",
        "gdb",
        "strace",
        "ltrace"
      };

      for (String process : debugProcesses) {
        try {
          executeAdbCommand(udid, "shell", "pkill", "-f", process);
          System.out.println("✓ " + process + " 프로세스 종료");
        } catch (IOException e) {
          // 프로세스가 없을 수 있음 (무시)
        }
      }

    } catch (Exception e) {
      System.out.println("⚠️  디버거 프로세스 종료 중 오류: " + e.getMessage());
    }
  }

  /**
   * 안드로이드 기본 제공 디버거 비활성화
   */
  public static void disableBuiltInDebugger(String udid) {
    System.out.println("\n--- 기본 제공 디버거 비활성화 ---");
    try {
      // 안드로이드 디버거 데몬(adbd) 비활성화
      executeAdbCommand(udid, "shell", "setprop", "persist.sys.usb.config", "mtp,adb");
      System.out.println("✓ adbd 재설정");

      // JDWP 포트 숨김
      executeAdbCommand(udid, "shell", "setprop", "ro.debuggable", "0");
      System.out.println("✓ JDWP 포트 비활성화");

    } catch (IOException e) {
      System.out.println("⚠️  기본 제공 디버거 비활성화 중 오류: " + e.getMessage());
    }
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
      process.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("adb 명령어 실행 중 중단됨", e);
    }

    return output.toString();
  }

  /**
   * 현재 시스템 속성들을 모두 확인합니다.
   */
  public static void dumpSystemProperties(String udid) {
    System.out.println("\n===== 시스템 속성 덤프 =====");
    try {
      String output = executeAdbCommand(udid, "shell", "getprop");
      System.out.println(output);
    } catch (IOException e) {
      System.err.println("시스템 속성 덤프 중 오류: " + e.getMessage());
    }
  }
}

