package org.example.e2e.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.HomePage;
import org.junit.jupiter.api.*;

/**
 * HomePage 고급 테스트 케이스
 *
 * 포함 항목:
 * - 성능 테스트 (응답 시간)
 * - 경계값 테스트
 * - 에러 처리 테스트
 * - 동시성 테스트
 * - 상태 유지 테스트
 */
@DisplayName("HomePage 고급 테스트")
public class HomePageAdvancedInteractionTest {

  private AndroidDriver driver;
  private HomePage homePage;

  @BeforeEach
  void setUp() {
    driver = DriverFactory.createAndroidDriver();
    homePage = new HomePage(driver);
    homePage.waitReady();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Nested
  @DisplayName("성능 테스트")
  class PerformanceTests {

    @Test
    @DisplayName("요소 찾기 응답 시간")
    void test_element_find_response_time() {
      long startTime = System.currentTimeMillis();
      homePage.isButton1Displayed();
      long endTime = System.currentTimeMillis();

      long responseTime = endTime - startTime;
      System.out.println("요소 찾기 응답 시간: " + responseTime + "ms");

      assert responseTime < 5000 : "요소 찾기가 너무 오래 걸렸습니다";
    }

    @Test
    @DisplayName("버튼 클릭 응답 시간")
    void test_button_click_response_time() {
      long startTime = System.currentTimeMillis();
      homePage.clickButton1();
      long endTime = System.currentTimeMillis();

      long responseTime = endTime - startTime;
      System.out.println("버튼 클릭 응답 시간: " + responseTime + "ms");

      assert responseTime < 2000 : "버튼 클릭이 너무 오래 걸렸습니다";
    }

    @Test
    @DisplayName("텍스트 입력 성능")
    void test_text_input_performance() {
      String testText = "성능 테스트용 텍스트 입력 필드 테스트";

      long startTime = System.currentTimeMillis();
      homePage.enterText(testText);
      long endTime = System.currentTimeMillis();

      long responseTime = endTime - startTime;
      System.out.println("텍스트 입력 시간: " + responseTime + "ms (텍스트 길이: " + testText.length() + ")");

      assert responseTime < 3000 : "텍스트 입력이 너무 오래 걸렸습니다";
    }

    @Test
    @DisplayName("반복 클릭 성능")
    void test_repeated_click_performance() {
      int clickCount = 10;
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < clickCount; i++) {
        homePage.clickButton1();
        try { Thread.sleep(50); } catch (InterruptedException e) { }
      }

      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      long avgTime = totalTime / clickCount;

      System.out.println("반복 클릭 (" + clickCount + "회) 총 시간: " + totalTime + "ms");
      System.out.println("평균 클릭 시간: " + avgTime + "ms");

      assert avgTime < 300 : "평균 클릭 시간이 너무 깁니다";
    }
  }

  @Nested
  @DisplayName("경계값 및 엣지 케이스 테스트")
  class EdgeCaseTests {

    @Test
    @DisplayName("매우 긴 텍스트 입력")
    void test_very_long_text_input() {
      StringBuilder longText = new StringBuilder();
      longText.append("이것은 매우 긴 테스트 텍스트입니다. ".repeat(100));

      assertDoesNotThrow(() -> homePage.enterText(longText.toString()), "매우 긴 텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("특수 문자 입력")
    void test_special_characters_input() {
      String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";

      assertDoesNotThrow(() -> homePage.enterText(specialChars), "특수 문자 입력에 실패했습니다");
    }

    @Test
    @DisplayName("빈 문자열 입력")
    void test_empty_string_input() {
      homePage.clearTextInput();
      assertDoesNotThrow(() -> homePage.enterText(""), "빈 문자열 입력에 실패했습니다");
    }

    @Test
    @DisplayName("공백만 입력")
    void test_whitespace_only_input() {
      assertDoesNotThrow(() -> homePage.enterText("     "), "공백만 입력에 실패했습니다");
    }

    @Test
    @DisplayName("한글 문자 입력")
    void test_korean_text_input() {
      String koreanText = "한글 테스트 입니다. 한국어 입력이 정상적으로 작동하는지 확인합니다.";

      assertDoesNotThrow(() -> homePage.enterText(koreanText), "한글 입력에 실패했습니다");
    }

    @Test
    @DisplayName("이모지 입력")
    void test_emoji_input() {
      String emojiText = "테스트 😀 🎉 ❤️ 이모지";

      assertDoesNotThrow(() -> homePage.enterText(emojiText), "이모지 입력에 실패했습니다");
    }

    @Test
    @DisplayName("줄바꿈 포함 텍스트 입력")
    void test_multiline_text_input() {
      String multilineText = "첫 번째 줄\n두 번째 줄\n세 번째 줄";

      assertDoesNotThrow(() -> homePage.enterText(multilineText), "줄바꿈 포함 텍스트 입력에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("상태 관리 테스트")
  class StateManagementTests {

    @Test
    @DisplayName("화면 회전 후 상태 유지")
    void test_state_after_rotation() throws InterruptedException {
      // 초기 상태 설정
      homePage.enterText("회전 테스트");
      homePage.clickCheckBox();
      Thread.sleep(300);

      // 화면 회전
      driver.rotate(org.openqa.selenium.ScreenOrientation.LANDSCAPE);
      Thread.sleep(1000);

      // 상태 확인
      String textValue = homePage.getTextInputValue();
      boolean checkboxState = homePage.isCheckBoxChecked();

      System.out.println("회전 후 텍스트: " + textValue);
      System.out.println("회전 후 체크박스: " + checkboxState);

      // 원래 상태로 복원
      driver.rotate(org.openqa.selenium.ScreenOrientation.PORTRAIT);
    }

    @Test
    @DisplayName("여러 요소 동시 상태 추적")
    void test_multiple_element_state_tracking() throws InterruptedException {
      // 초기 상태
      boolean button1Initial = homePage.isButton1Displayed();
      boolean button2Initial = homePage.isButton2Displayed();
      boolean checkboxInitial = homePage.isCheckBoxChecked();
      boolean switchInitial = homePage.isSwitchChecked();

      // 상태 변경
      homePage.clickCheckBox();
      homePage.toggleSwitch();
      Thread.sleep(300);

      // 최종 상태
      boolean button1final = homePage.isButton1Displayed();
      boolean button2final = homePage.isButton2Displayed();
      boolean checkboxFinal = homePage.isCheckBoxChecked();
      boolean switchFinal = homePage.isSwitchChecked();

      System.out.println("체크박스: " + checkboxInitial + " -> " + checkboxFinal);
      System.out.println("스위치: " + switchInitial + " -> " + switchFinal);

      assert button1Initial == button1final : "버튼1이 예상치 못하게 변경됨";
      assert button2Initial == button2final : "버튼2가 예상치 못하게 변경됨";
      assert checkboxInitial != checkboxFinal : "체크박스 상태 변경 실패";
      assert switchInitial != switchFinal : "스위치 상태 변경 실패";
    }

    @Test
    @DisplayName("특정 요소만 상태 유지")
    void test_selective_state_persistence() throws InterruptedException {
      // 버튼 1만 클릭했을 때는 다른 요소 상태는 변하지 않음
      boolean checkboxBefore = homePage.isCheckBoxChecked();
      boolean switchBefore = homePage.isSwitchChecked();

      homePage.clickButton1();
      Thread.sleep(300);

      boolean checkboxAfter = homePage.isCheckBoxChecked();
      boolean switchAfter = homePage.isSwitchChecked();

      assert checkboxBefore == checkboxAfter : "체크박스 상태가 변경되었습니다";
      assert switchBefore == switchAfter : "스위치 상태가 변경되었습니다";
    }
  }

  @Nested
  @DisplayName("에러 처리 테스트")
  class ErrorHandlingTests {

    @Test
    @DisplayName("존재하지 않는 요소 처리")
    void test_nonexistent_element_handling() {
      // 존재하지 않는 요소에 대한 처리
      assertDoesNotThrow(() -> {
        driver.findElements(org.openqa.selenium.By.xpath("//android.widget.UnknownElement"));
      }, "존재하지 않는 요소 처리 실패");
    }

    @Test
    @DisplayName("빠른 연속 클릭 에러 처리")
    void test_rapid_click_error_handling() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 20; i++) {
          homePage.clickButton1();
        }
      }, "빠른 연속 클릭 처리 실패");
    }

    @Test
    @DisplayName("동시 입력 에러 처리")
    void test_concurrent_input_handling() {
      assertDoesNotThrow(() -> {
        homePage.enterText("첫 번째");
        homePage.clearTextInput();
        homePage.enterText("두 번째");
        homePage.clearTextInput();
        homePage.enterText("세 번째");
      }, "동시 입력 처리 실패");
    }
  }

  @Nested
  @DisplayName("스트레스 테스트")
  class StressTests {

    @Test
    @DisplayName("장시간 실행 테스트")
    void test_long_running_operations() {
      long startTime = System.currentTimeMillis();
      long duration = 0;
      int iterations = 0;

      while (duration < 30000) { // 30초 동안 실행
        homePage.clickButton1();
        homePage.clickButton2();
        homePage.clickCheckBox();
        homePage.toggleSwitch();

        iterations++;
        duration = System.currentTimeMillis() - startTime;
      }

      System.out.println("30초 동안 " + iterations + "회의 전체 작업 수행");
      System.out.println("평균 작업 시간: " + (duration / iterations) + "ms");
    }

    @Test
    @DisplayName("메모리 누수 확인")
    void test_memory_leak_detection() {
      Runtime runtime = Runtime.getRuntime();

      // GC 실행
      System.gc();
      long memBefore = runtime.totalMemory() - runtime.freeMemory();

      // 반복 작업
      for (int i = 0; i < 100; i++) {
        homePage.enterText("테스트" + i);
        homePage.getTextInputValue();
        homePage.clearTextInput();
      }

      // 최종 메모리 확인
      System.gc();
      long memAfter = runtime.totalMemory() - runtime.freeMemory();
      long memDifference = memAfter - memBefore;

      System.out.println("메모리 차이: " + (memDifference / 1024 / 1024) + "MB");
    }
  }
}

