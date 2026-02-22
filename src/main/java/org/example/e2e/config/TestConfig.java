package org.example.e2e.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class TestConfig {
  public static String appiumServerUrl() {
    return System.getProperty("APPIUM_URL", "http://127.0.0.1:4723");
  }

  public static String deviceName() {
    return System.getProperty("DEVICE_NAME", "Android Emulator");
  }

  public static String platformName() {
    return System.getProperty("PLATFORM_NAME", "Android");
  }

  public static String appPath() {
    // 1) 최우선: -DAPP_PATH=... 로 명시한 경우
    String explicit = System.getProperty("APP_PATH");
    if (explicit != null && !explicit.isBlank()) return explicit;

    // 2) 다음: 프로젝트 루트 기반 apk/*.apk 자동 탐색
    String projectDir = System.getProperty("PROJECT_DIR", System.getProperty("user.dir"));
    Path apkDir = Paths.get(projectDir, "apk");

    if (!Files.isDirectory(apkDir)) {
      throw new IllegalArgumentException("apk directory not found: " + apkDir.toAbsolutePath());
    }

    try (Stream<Path> s = Files.list(apkDir)) {
      Optional<Path> newestApk = s
          .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".apk"))
          // 여러 개면 마지막 수정시간이 가장 최근인 apk 선택
          .max(Comparator.comparingLong(p -> {
            try { return Files.getLastModifiedTime(p).toMillis(); }
            catch (IOException e) { return 0L; }
          }));

      Path apk = newestApk.orElseThrow(() ->
          new IllegalArgumentException("No .apk found in: " + apkDir.toAbsolutePath()));

      return apk.toAbsolutePath().toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to scan apk directory: " + apkDir.toAbsolutePath(), e);
    }
  }

  public static String udid() {
    // 실기기면 필요. 에뮬레이터도 지정 가능.
    return System.getProperty("UDID", "");
  }

  public static String appPackage() {
    return System.getProperty("APP_PACKAGE", "");
  }

  public static String appActivity() {
    return System.getProperty("APP_ACTIVITY", "");
  }

  public static int newCommandTimeoutSec() {
    return Integer.parseInt(System.getProperty("NEW_COMMAND_TIMEOUT", "120"));
  }
}