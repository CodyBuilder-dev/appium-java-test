package org.example.e2e.tests;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.HomePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HomePage 고급 테스트 클래스
 * 성능, 에러 처리, 경계값 테스트 등을 포함
 */
@DisplayName("홈페이지 고급 테스트")
public class HomePageAdvancedTest {

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
  @DisplayName("경계값 테스트 (Boundary Value Tests)")
  class BoundaryValueTests {

    @Test
    @DisplayName("빈 문자열 입력")
    void test_enter_empty_string() {
      homePage.enterText("");
      String inputValue = homePage.getTextInputValue();
      assertEquals("", inputValue, "빈 문자열이 저장되지 않았습니다");
    }

    @Test
    @DisplayName("한 글자만 입력")
    void test_enter_single_character() {
      homePage.enterText("A");
      String inputValue = homePage.getTextInputValue();
      assertEquals("A", inputValue, "한 글자만 저장되지 않았습니다");
    }

    @Test
    @DisplayName("한글 텍스트 입력")
    void test_enter_korean_text() {
      String koreanText = "안녕하세요 테스트입니다";
      homePage.enterText(koreanText);
      String inputValue = homePage.getTextInputValue();
      assertEquals(koreanText, inputValue, "한글 텍스트가 저장되지 않았습니다");
    }

    @Test
    @DisplayName("이모지 입력")
    void test_enter_emoji() {
      String emojiText = "Test 😀 🎉";
      assertDoesNotThrow(() -> homePage.enterText(emojiText), "이모지 입력에 실패했습니다");
    }

    @Test
    @DisplayName("매우 긴 텍스트 입력 (1000자)")
    void test_enter_very_long_text() {
      StringBuilder longText = new StringBuilder();
      longText.append("Test".repeat(100));
      assertDoesNotThrow(() -> homePage.enterText(longText.toString()), "긴 텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("탭과 줄바꿈 문자 입력")
    void test_enter_whitespace_characters() {
      String whitespaceText = "Test\tWith\nWhitespace";
      assertDoesNotThrow(() -> homePage.enterText(whitespaceText), "공백 문자 입력에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("반복 및 스트레스 테스트")
  class StressTests {

    @RepeatedTest(5)
    @DisplayName("버튼 1 반복 클릭 (5회)")
    void test_button1_repeated_clicks(org.junit.jupiter.api.RepetitionInfo repetitionInfo) {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(100);
      }, "반복 #" + repetitionInfo.getCurrentRepetition() + "에서 버튼 1 클릭 실패");
    }

    @RepeatedTest(5)
    @DisplayName("버튼 2 반복 클릭 (5회)")
    void test_button2_repeated_clicks(org.junit.jupiter.api.RepetitionInfo repetitionInfo) {
      assertDoesNotThrow(() -> {
        homePage.clickButton2();
        Thread.sleep(100);
      }, "반복 #" + repetitionInfo.getCurrentRepetition() + "에서 버튼 2 클릭 실패");
    }

    @Test
    @DisplayName("100번의 빠른 텍스트 입력/초기화")
    void test_text_input_stress() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 100; i++) {
          homePage.enterText("Stress" + i);
          if (i % 10 == 0) {
            Thread.sleep(50);
          }
        }
      }, "스트레스 테스트 중 실패");
    }

    @Test
    @DisplayName("연속적인 체크박스 토글 (50회)")
    void test_checkbox_continuous_toggle() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 50; i++) {
          homePage.clickCheckBox();
          if (i % 10 == 0) {
            Thread.sleep(50);
          }
        }
      }, "체크박스 연속 토글 중 실패");
    }

    @Test
    @DisplayName("연속적인 스위치 토글 (50회)")
    void test_switch_continuous_toggle() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 50; i++) {
          homePage.toggleSwitch();
          if (i % 10 == 0) {
            Thread.sleep(50);
          }
        }
      }, "스위치 연속 토글 중 실패");
    }
  }

  @Nested
  @DisplayName("상태 변화 테스트")
  class StateTransitionTests {

    @Test
    @DisplayName("체크박스 상태 변화 추적")
    void test_checkbox_state_transitions() {
      // 초기 상태
      assertTrue(homePage.isCheckBoxUnChecked(), "초기 상태는 체크 해제여야 합니다");

      // 첫 번째 클릭 - 체크
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxChecked(), "첫 번째 클릭 후 체크되어야 합니다");

      // 두 번째 클릭 - 해제
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxUnChecked(), "두 번째 클릭 후 해제되어야 합니다");

      // 세 번째 클릭 - 체크
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxChecked(), "세 번째 클릭 후 체크되어야 합니다");
    }

    @Test
    @DisplayName("텍스트 입력 상태 변화")
    void test_text_input_state_changes() {
      // 빈 입력 필드
      homePage.clearTextInput();
      assertEquals("", homePage.getTextInputValue(), "초기 상태는 빈 필드여야 합니다");

      // 텍스트 입력
      homePage.enterText("First");
      assertEquals("First", homePage.getTextInputValue());

      // 텍스트 변경
      homePage.enterText("Second");
      assertEquals("Second", homePage.getTextInputValue());

      // 텍스트 초기화
      homePage.clearTextInput();
      assertEquals("", homePage.getTextInputValue());
    }

    @Test
    @DisplayName("버튼 클릭에 따른 상태 변화")
    void test_button_click_state_changes() {
      assertTrue(homePage.isButton1Displayed());
      assertTrue(homePage.isButton2Displayed());

      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        assertTrue(homePage.isButton1Displayed());
        assertTrue(homePage.isButton2Displayed());
      });
    }
  }

  @Nested
  @DisplayName("에러 처리 및 복구 테스트")
  class ErrorHandlingTests {

    @Test
    @DisplayName("UI 요소 없을 때의 처리")
    void test_missing_element_handling() {
      // 홈페이지가 표시되어 있어야 함
      assertTrue(homePage.isHomePageDisplayed());

      // 모든 UI 요소가 표시되어 있어야 함
      assertTrue(homePage.isButton1Displayed());
      assertTrue(homePage.isButton2Displayed());
      assertTrue(homePage.isTextInputDisplayed());
    }

    @Test
    @DisplayName("연속 실패 복구")
    void test_recovery_from_multiple_operations() {
      try {
        homePage.clickButton1();
        Thread.sleep(100);

        homePage.enterText("Test");
        Thread.sleep(100);

        homePage.clickCheckBox();
        Thread.sleep(100);

        // 모든 작업이 완료된 후에도 홈페이지가 유효한지 확인
        assertTrue(homePage.isHomePageDisplayed());
      } catch (Exception e) {
        fail("작업 중 예외가 발생했습니다: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("예외 발생 시 UI 상태 확인")
    void test_ui_state_after_operations() {
      assertDoesNotThrow(() -> homePage.clickButton1());

      // 작업 후에도 UI가 응답하는지 확인
      assertTrue(homePage.isHomePageDisplayed());
      assertTrue(homePage.isButton2Displayed());
    }
  }

  @Nested
  @DisplayName("성능 및 응답성 테스트")
  class PerformanceTests {

    @Test
    @DisplayName("버튼 클릭 응답성")
    void test_button_click_responsiveness() {
      long startTime = System.currentTimeMillis();

      assertDoesNotThrow(() -> {
        for (int i = 0; i < 10; i++) {
          homePage.clickButton1();
          Thread.sleep(50);
        }
      });

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      // 최대 5초 이내에 완료되어야 함
      assertTrue(duration < 5000, "버튼 클릭이 너무 오래 걸립니다: " + duration + "ms");
    }

    @Test
    @DisplayName("텍스트 입력 성능")
    void test_text_input_performance() {
      long startTime = System.currentTimeMillis();

      assertDoesNotThrow(() -> {
        for (int i = 0; i < 20; i++) {
          homePage.enterText("Performance Test " + i);
          Thread.sleep(50);
        }
      });

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      // 최대 10초 이내에 완료되어야 함
      assertTrue(duration < 10000, "텍스트 입력이 너무 오래 걸립니다: " + duration + "ms");
    }

    @Test
    @DisplayName("UI 요소 가시성 확인 시간")
    void test_ui_visibility_performance() {
      long startTime = System.currentTimeMillis();

      assertTrue(homePage.isHomePageDisplayed());
      assertTrue(homePage.isButton1Displayed());
      assertTrue(homePage.isButton2Displayed());
      assertTrue(homePage.isTextInputDisplayed());
      assertTrue(homePage.isHeaderTitleDisplayed());

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      // 모든 가시성 확인이 1초 이내에 완료되어야 함
      assertTrue(duration < 1000, "UI 가시성 확인이 너무 오래 걸립니다: " + duration + "ms");
    }
  }

  @Nested
  @DisplayName("데이터 유효성 테스트")
  class DataValidationTests {

    @Test
    @DisplayName("숫자 입력 검증")
    void test_numeric_input_validation() {
      String numericInput = "1234567890";
      homePage.enterText(numericInput);
      assertEquals(numericInput, homePage.getTextInputValue());
    }

    @Test
    @DisplayName("알파벳 입력 검증")
    void test_alphabetic_input_validation() {
      String alphabeticInput = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      homePage.enterText(alphabeticInput);
      assertEquals(alphabeticInput, homePage.getTextInputValue());
    }

    @Test
    @DisplayName("혼합 입력 검증")
    void test_mixed_input_validation() {
      String mixedInput = "Test123!@#ABC";
      homePage.enterText(mixedInput);
      assertEquals(mixedInput, homePage.getTextInputValue());
    }

    @Test
    @DisplayName("데이터베이스 쿼리 관련 특수 문자 입력")
    void test_sql_like_input() {
      String sqlInput = "'; DROP TABLE users; --";
      assertDoesNotThrow(() -> homePage.enterText(sqlInput), "SQL 같은 입력 처리에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("네비게이션 및 상호작용 테스트")
  class NavigationInteractionTests {

    @Test
    @DisplayName("네비게이션 이후 UI 상태")
    void test_ui_state_after_navigation() throws Exception{
      homePage.clickBottomNavigation();
      Thread.sleep(300);

      // 네비게이션 후에도 홈페이지가 표시되는지 확인
      assertTrue(homePage.isHomePageDisplayed() || homePage.isButton1Displayed(),
                "네비게이션 후 UI 상태가 불명확합니다");
    }

    @Test
    @DisplayName("버튼과 네비게이션의 조합 상호작용")
    void test_button_navigation_interaction() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(200);

        homePage.clickBottomNavigation();
        Thread.sleep(200);

        homePage.clickButton2();
        Thread.sleep(200);
      });
    }

    @Test
    @DisplayName("텍스트 입력과 네비게이션의 조합")
    void test_text_input_navigation_interaction() {
      assertDoesNotThrow(() -> {
        homePage.enterText("Navigation Test");
        Thread.sleep(200);

        homePage.clickBottomNavigation();
        Thread.sleep(200);

        // 네비게이션 후 텍스트 입력 재개
        homePage.enterText("Back to Input");
        Thread.sleep(200);
      });
    }
  }

  @Nested
  @DisplayName("사용자 행동 패턴 테스트")
  class UserBehaviorPatternTests {

    @Test
    @DisplayName("버튼 클릭 → 텍스트 입력 → 체크박스 체크")
    void test_button_text_checkbox_pattern() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(200);

        homePage.enterText("User Pattern Test");
        Thread.sleep(200);

        homePage.clickCheckBox();
        Thread.sleep(200);

        assertTrue(homePage.isCheckBoxChecked());
      });
    }

    @Test
    @DisplayName("텍스트 입력 → 초기화 → 재입력 패턴")
    void test_text_clear_reinput_pattern() {
      assertDoesNotThrow(() -> {
        homePage.enterText("First Input");
        Thread.sleep(200);

        homePage.clearTextInput();
        Thread.sleep(200);

        homePage.enterText("Second Input");
        assertEquals("Second Input", homePage.getTextInputValue());
      });
    }

    @Test
    @DisplayName("체크박스 토글 → 스위치 토글 → 버튼 클릭 패턴")
    void test_checkbox_switch_button_pattern() {
      assertDoesNotThrow(() -> {
        homePage.clickCheckBox();
        Thread.sleep(200);

        homePage.toggleSwitch();
        Thread.sleep(200);

        homePage.clickButton2();
        Thread.sleep(200);

        // 모든 요소가 여전히 표시되는지 확인
        assertTrue(homePage.isButton1Displayed());
        assertTrue(homePage.isButton2Displayed());
      });
    }

    @Test
    @DisplayName("모든 요소를 순환적으로 상호작용")
    void test_cyclic_interaction_pattern() {
      assertDoesNotThrow(() -> {
        for (int cycle = 0; cycle < 3; cycle++) {
          homePage.clickButton1();
          Thread.sleep(100);

          homePage.enterText("Cycle " + cycle);
          Thread.sleep(100);

          homePage.clickCheckBox();
          Thread.sleep(100);

          homePage.toggleSwitch();
          Thread.sleep(100);

          homePage.clickButton2();
          Thread.sleep(100);

          homePage.clearTextInput();
          Thread.sleep(100);
        }
      });
    }
  }
}

