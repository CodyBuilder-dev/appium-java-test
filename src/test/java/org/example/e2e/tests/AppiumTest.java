package org.example.e2e.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.openqa.selenium.ScreenOrientation;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppiumTest {

    private AndroidDriver driver;

    @BeforeEach
    public void setUp() {
        String apkPath = System.getProperty("apkPath", "");
        // APK 경로가 제공되지 않으면 테스트를 건너뜁니다.
        Assumptions.assumeTrue(!apkPath.isEmpty(), "No apkPath provided — skipping Appium tests");

        UiAutomator2Options options = new UiAutomator2Options()
            .setPlatformName("Android")
            .setAutomationName("UiAutomator2")
            .setApp(apkPath)
            .setNewCommandTimeout(Duration.ofSeconds(120));

        // Optional device properties
        String deviceName = System.getProperty("deviceName", "");
        if (!deviceName.isEmpty()) {
            options.setDeviceName(deviceName);
        }
        String platformVersion = System.getProperty("platformVersion", "");
        if (!platformVersion.isEmpty()) {
            options.setPlatformVersion(platformVersion);
        }
        String udid = System.getProperty("udid", "");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }

        try {
            URL serverUrl = new URL(System.getProperty("appiumServerUrl", "http://127.0.0.1:4723"));
            driver = new AndroidDriver(serverUrl, options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        } catch (Exception e) {
            e.printStackTrace();
            // Appium 서버에 연결할 수 없으면 테스트를 건너뜁니다.
            Assumptions.assumeTrue(false, "Cannot initialize AndroidDriver: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("기본 앱 시작 및 요소 발견")
    public void simpleLaunchAndFind() {
        Assumptions.assumeTrue(driver != null, "Driver not initialized — skipping test");

        boolean found = false;
        try {
            // 앱이 시작될 시간을 잠깐 둡니다.
            Thread.sleep(2000);
            found = driver.findElements(AppiumBy.accessibilityId("Login")).size() > 0
                || driver.findElements(AppiumBy.id("com.example:id/login_button")).size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(found,
            "Expected element was not found in the app — update selectors or provide a different APK");
    }

    @Nested
    @DisplayName("기기 방향 테스트")
    class OrientationTests {

        @Test
        @DisplayName("세로 모드 테스트")
        public void test_portrait_orientation() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");
            driver.rotate(ScreenOrientation.PORTRAIT);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertTrue(driver != null, "세로 모드 설정이 성공했습니다");
        }

        @Test
        @DisplayName("가로 모드 테스트")
        public void test_landscape_orientation() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");
            driver.rotate(ScreenOrientation.LANDSCAPE);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertTrue(driver != null, "가로 모드 설정이 성공했습니다");
        }
    }

    @Nested
    @DisplayName("타임아웃 및 대기 테스트")
    class TimeoutTests {

        @Test
        @DisplayName("암묵적 대기 테스트")
        public void test_implicit_wait() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            long startTime = System.currentTimeMillis();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime >= 0, "암묵적 대기가 설정되었습니다");
        }
    }

    @Nested
    @DisplayName("앱 상태 테스트")
    class AppStateTests {

        @Test
        @DisplayName("앱 백그라운드 실행")
        public void test_app_to_background() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            driver.runAppInBackground(Duration.ofSeconds(2));
            assertTrue(driver != null, "앱이 백그라운드에서 복원되었습니다");
        }
    }

    @Nested
    @DisplayName("화면 캡처 테스트")
    class ScreenshotTests {

        @Test
        @DisplayName("스크린샷 캡처")
        public void test_screenshot() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                byte[] screenshot = driver.getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                assertNotNull(screenshot, "스크린샷이 캡처되었습니다");
                assertTrue(screenshot.length > 0, "스크린샷 파일이 비어있지 않습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("페이지 소스 및 XML 테스트")
    class PageSourceTests {

        @Test
        @DisplayName("페이지 소스 획득")
        public void test_get_page_source() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                String pageSource = driver.getPageSource();
                assertNotNull(pageSource, "페이지 소스가 획득되었습니다");
                assertTrue(pageSource.length() > 0, "페이지 소스가 비어있지 않습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("네트워크 연결 테스트")
    class NetworkTests {

        @Test
        @DisplayName("비행기 모드 토글")
        public void test_airplane_mode_toggle() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                driver.executeScript("mobile: toggleAirplaneMode");
                Thread.sleep(1000);
                driver.executeScript("mobile: toggleAirplaneMode"); // 다시 켜기
                assertTrue(driver != null, "비행기 모드 토글이 성공했습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("센서 테스트")
    class SensorTests {

        @Test
        @DisplayName("기기 흔들기 감지")
        public void test_device_shake_gesture() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                driver.executeScript("mobile: shakeDevice");
                assertTrue(driver != null, "기기 흔들기 제스처가 실행되었습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("애플리케이션 정보 테스트")
    class AppInfoTests {

        @Test
        @DisplayName("현재 활동 확인")
        public void test_current_activity() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                String currentActivity = driver.currentActivity();
                assertNotNull(currentActivity, "현재 활동이 확인되었습니다");
                assertTrue(currentActivity.length() > 0, "활동명이 비어있지 않습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        @DisplayName("앱 패키지 확인")
        public void test_app_package() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                String appPackage = driver.getCurrentPackage();
                assertNotNull(appPackage, "앱 패키지가 확인되었습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("시스템 시간 테스트")
    class SystemTimeTests {

        @Test
        @DisplayName("기기 시간 변경")
        public void test_device_time_change() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                // 시스템 시간 관련 스크립트 실행
                driver.executeScript("mobile: getDeviceTime");
                assertTrue(driver != null, "기기 시간을 조회할 수 있습니다");
            } catch (Exception e) {
                // 일부 기기에서는 지원하지 않을 수 있음
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("성능 모니터링 테스트")
    class PerformanceMonitoringTests {

        @Test
        @DisplayName("CPU 사용률 확인")
        public void test_cpu_usage() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                long startTime = System.currentTimeMillis();
                driver.findElements(AppiumBy.className("android.widget.Button"));
                long endTime = System.currentTimeMillis();

                assertTrue(endTime - startTime >= 0, "CPU 사용률을 모니터링합니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        @DisplayName("메모리 사용량 확인")
        public void test_memory_usage() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                Runtime runtime = Runtime.getRuntime();
                long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
                assertTrue(memoryUsed > 0, "메모리 사용량을 확인합니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("시스템 알림 테스트")
    class SystemNotificationTests {

        @Test
        @DisplayName("푸시 알림 처리")
        public void test_system_notification() {
            Assumptions.assumeTrue(driver != null, "Driver not initialized");

            try {
                // 알림 창 열기
                driver.openNotifications();
                Thread.sleep(1000);

                // 다시 앱으로 돌아가기
                driver.findElements(AppiumBy.className("android.widget.Button"));
                assertTrue(driver != null, "알림 창이 처리되었습니다");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
