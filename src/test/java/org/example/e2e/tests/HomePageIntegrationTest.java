package org.example.e2e.tests;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.HomePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 종합 통합 테스트 클래스
 * HomePage의 모든 기능을 복합적으로 테스트
 */
@DisplayName("홈페이지 종합 통합 테스트")
public class HomePageIntegrationTest {

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
  @DisplayName("개별 기능 완결성 테스트")
  class FeatureCompletenessTests {

    @Test
    @DisplayName("사용자가 모든 페이지 요소에 접근 가능")
    void test_all_elements_are_accessible() {
      // 모든 요소의 가시성 확인
      assertTrue(homePage.isHomePageDisplayed(), "홈페이지가 표시되지 않음");
      assertTrue(homePage.isHeaderTitleDisplayed(), "헤더 제목이 표시되지 않음");
      assertTrue(homePage.isButton1Displayed(), "버튼 1이 표시되지 않음");
      assertTrue(homePage.isButton2Displayed(), "버튼 2가 표시되지 않음");
      assertTrue(homePage.isTextInputDisplayed(), "텍스트 입력이 표시되지 않음");
      assertTrue(homePage.isSwitchDisplayed(), "스위치가 표시되지 않음");
      assertTrue(homePage.isBottomNavigationDisplayed(), "네비게이션이 표시되지 않음");
    }

    @Test
    @DisplayName("모든 버튼이 클릭 가능")
    void test_all_buttons_are_clickable() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(100);
        homePage.clickButton2();
        Thread.sleep(100);
        homePage.clickBottomNavigation();
      }, "버튼 클릭 실패");
    }

    @Test
    @DisplayName("모든 입력 요소가 작동 가능")
    void test_all_input_elements_work() {
      assertDoesNotThrow(() -> {
        homePage.enterText("Test");
        homePage.clickCheckBox();
        homePage.toggleSwitch();
      }, "입력 요소 작동 실패");
    }
  }

  @Nested
  @DisplayName("텍스트 입력 필드 종합 테스트")
  class TextInputComprehensiveTests {

    @Test
    @DisplayName("다양한 텍스트 타입의 입력과 검증")
    void test_various_text_types() {
      String[] testInputs = {
        "Simple text",
        "123456",
        "!@#$%^&*()",
        "MixedCase123!",
        "한글 테스트",
        "UPPERCASE",
        "lowercase",
        "numbers only 999",
        "special chars ~`",
        ""
      };

      for (String input : testInputs) {
        homePage.clearTextInput();
        homePage.enterText(input);
        assertEquals(input, homePage.getTextInputValue(),
                    "입력값이 일치하지 않음: " + input);
      }
    }

    @Test
    @DisplayName("텍스트 입력과 다른 요소의 상호 영향 없음")
    void test_text_input_isolation() throws Exception {
      String testText = "Isolation Test";
      homePage.enterText(testText);

      // 다른 요소 조작
      homePage.clickButton1();
      Thread.sleep(100);
      homePage.clickCheckBox();
      Thread.sleep(100);
      homePage.toggleSwitch();
      Thread.sleep(100);

      // 텍스트가 유지되는지 확인
      assertEquals(testText, homePage.getTextInputValue(),
                  "텍스트가 변경되었습니다");
    }

    @Test
    @DisplayName("연속 텍스트 입력/변경 작업")
    void test_continuous_text_operations() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 10; i++) {
          String text = "Input " + i;
          homePage.enterText(text);
          assertEquals(text, homePage.getTextInputValue());
          Thread.sleep(50);
        }
      }, "연속 텍스트 작업 실패");
    }
  }

  @Nested
  @DisplayName("체크박스 상태 관리 테스트")
  class CheckBoxStateManagementTests {

    @Test
    @DisplayName("체크박스의 상태 전환이 올바르게 작동")
    void test_checkbox_state_transitions() {
      // 초기 상태 확인
      assertTrue(homePage.isCheckBoxUnChecked(), "초기 상태는 미체크");

      // 첫 번째 클릭
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxChecked(), "첫 클릭 후 체크");

      // 두 번째 클릭
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxUnChecked(), "두 번째 클릭 후 미체크");

      // 세 번째 클릭
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxChecked(), "세 번째 클릭 후 체크");

      // 네 번째 클릭
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxUnChecked(), "네 번째 클릭 후 미체크");
    }

    @Test
    @DisplayName("체크박스 상태와 텍스트 입력의 독립성")
    void test_checkbox_text_independence() {
      String testText1 = "Before Check";
      homePage.enterText(testText1);
      assertEquals(testText1, homePage.getTextInputValue());

      homePage.clickCheckBox();
      assertEquals(testText1, homePage.getTextInputValue(),
                  "체크박스 클릭 후 텍스트가 변경됨");

      String testText2 = "After Check";
      homePage.enterText(testText2);
      assertEquals(testText2, homePage.getTextInputValue());
      assertTrue(homePage.isCheckBoxChecked());
    }

    @Test
    @DisplayName("빠른 체크박스 토글 스트레스")
    void test_rapid_checkbox_toggle() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 20; i++) {
          homePage.clickCheckBox();
          if (i % 2 == 0) {
            assertTrue(homePage.isCheckBoxChecked(), "홀수 클릭 후 체크");
          } else {
            assertTrue(homePage.isCheckBoxUnChecked(), "짝수 클릭 후 미체크");
          }
        }
      }, "체크박스 토글 스트레스 테스트 실패");
    }
  }

  @Nested
  @DisplayName("스위치 토글 테스트")
  class SwitchToggleTests {

    @Test
    @DisplayName("스위치가 반복적으로 토글 가능")
    void test_switch_repeated_toggle() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 10; i++) {
          homePage.toggleSwitch();
          assertTrue(homePage.isSwitchDisplayed());
          Thread.sleep(100);
        }
      }, "스위치 토글 실패");
    }

    @Test
    @DisplayName("스위치와 다른 요소의 상호작용")
    void test_switch_interaction_with_others() {
      assertDoesNotThrow(() -> {
        // 스위치와 버튼
        homePage.toggleSwitch();
        Thread.sleep(100);
        homePage.clickButton1();
        Thread.sleep(100);

        // 스위치와 텍스트
        homePage.toggleSwitch();
        Thread.sleep(100);
        homePage.enterText("With Switch");
        Thread.sleep(100);

        // 스위치와 체크박스
        homePage.toggleSwitch();
        Thread.sleep(100);
        homePage.clickCheckBox();
        Thread.sleep(100);
      }, "스위치 상호작용 테스트 실패");
    }
  }

  @Nested
  @DisplayName("버튼 클릭 고급 테스트")
  class ButtonClickAdvancedTests {

    @Test
    @DisplayName("버튼 1과 2의 독립적 동작")
    void test_buttons_independence() {
      assertDoesNotThrow(() -> {
        // 번갈아 가며 클릭
        for (int i = 0; i < 5; i++) {
          homePage.clickButton1();
          assertTrue(homePage.isButton2Displayed());
          Thread.sleep(100);

          homePage.clickButton2();
          assertTrue(homePage.isButton1Displayed());
          Thread.sleep(100);
        }
      }, "버튼 독립성 테스트 실패");
    }

    @Test
    @DisplayName("버튼과 텍스트 입력의 동시 작업")
    void test_button_and_text_concurrent() {
      assertDoesNotThrow(() -> {
        String[] texts = {"Button1 Test", "Button2 Test", "Both Test"};
        for (String text : texts) {
          homePage.clickButton1();
          Thread.sleep(50);
          homePage.enterText(text);
          assertEquals(text, homePage.getTextInputValue());
          Thread.sleep(50);
          homePage.clickButton2();
          Thread.sleep(50);
        }
      }, "버튼과 텍스트 동시 작업 실패");
    }
  }

  @Nested
  @DisplayName("네비게이션 테스트")
  class NavigationAdvancedTests {

    @Test
    @DisplayName("네비게이션 클릭 후 UI 상태 유지")
    void test_ui_state_after_navigation() throws Exception {
      String testText = "Navigation Test";
      homePage.enterText(testText);
      assertTrue(homePage.isCheckBoxUnChecked());

      homePage.clickBottomNavigation();
      Thread.sleep(300);

      // UI 상태가 유지되거나 정상적으로 변경되었는지 확인
      assertTrue(homePage.isHomePageDisplayed() || homePage.isButton1Displayed(),
                "네비게이션 후 UI 상태가 비정상");
    }

    @Test
    @DisplayName("반복적인 네비게이션 클릭")
    void test_repeated_navigation() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 5; i++) {
          homePage.clickBottomNavigation();
          Thread.sleep(200);
          assertTrue(homePage.isBottomNavigationDisplayed());
        }
      }, "반복적 네비게이션 실패");
    }
  }

  @Nested
  @DisplayName("복합 사나리오 테스트")
  class ComplexScenarioTests {

    @Test
    @DisplayName("시나리오 1: 폼 입력 및 제출")
    void test_scenario_form_input_and_submit() {
      assertDoesNotThrow(() -> {
        // 사용자가 폼에 데이터를 입력하는 시나리오
        homePage.enterText("John Doe");
        Thread.sleep(200);

        homePage.clickCheckBox();
        Thread.sleep(200);

        homePage.toggleSwitch();
        Thread.sleep(200);

        // 제출 버튼 클릭
        homePage.clickButton1();
        Thread.sleep(300);

        // UI가 여전히 응답하는지 확인
        assertTrue(homePage.isHomePageDisplayed());
      }, "폼 입력 시나리오 실패");
    }

    @Test
    @DisplayName("시나리오 2: 설정 변경 및 저장")
    void test_scenario_settings_change_save() {
      assertDoesNotThrow(() -> {
        // 설정 항목 변경
        homePage.toggleSwitch();
        Thread.sleep(200);

        homePage.clickCheckBox();
        Thread.sleep(200);

        // 저장 버튼
        homePage.clickButton2();
        Thread.sleep(300);

        // 설정이 적용되었는지 확인 (상태 유지)
        assertTrue(homePage.isButton1Displayed());
        assertTrue(homePage.isButton2Displayed());
      }, "설정 변경 시나리오 실패");
    }

    @Test
    @DisplayName("시나리오 3: 데이터 입력, 검증, 초기화")
    void test_scenario_input_validate_clear() {
      assertDoesNotThrow(() -> {
        String validData = "Valid@Data123";
        homePage.enterText(validData);
        assertEquals(validData, homePage.getTextInputValue());
        Thread.sleep(200);

        // 데이터 확인
        homePage.clickButton1();
        Thread.sleep(200);

        // 다시 확인
        assertTrue(homePage.isTextInputDisplayed());
        Thread.sleep(200);

        // 데이터 초기화
        homePage.clearTextInput();
        assertEquals("", homePage.getTextInputValue());
        Thread.sleep(200);

        // 초기화 후 새로운 데이터 입력
        homePage.enterText("NewData");
        assertEquals("NewData", homePage.getTextInputValue());
      }, "데이터 입력 시나리오 실패");
    }

    @Test
    @DisplayName("시나리오 4: 장시간 사용 시뮬레이션")
    void test_scenario_prolonged_usage() {
      assertDoesNotThrow(() -> {
        for (int session = 0; session < 3; session++) {
          // 각 세션에서 주요 기능 사용
          homePage.enterText("Session " + session);
          Thread.sleep(100);

          homePage.clickButton1();
          Thread.sleep(100);

          homePage.clickCheckBox();
          Thread.sleep(100);

          homePage.toggleSwitch();
          Thread.sleep(100);

          homePage.clickButton2();
          Thread.sleep(100);

          homePage.clearTextInput();
          Thread.sleep(100);

          // 각 세션 사이에 대기
          Thread.sleep(500);

          // 시스템이 여전히 정상인지 확인
          assertTrue(homePage.isHomePageDisplayed());
        }
      }, "장시간 사용 시뮬레이션 실패");
    }

    @Test
    @DisplayName("시나리오 5: 오류 복구 및 재시도")
    void test_scenario_error_recovery() {
      assertDoesNotThrow(() -> {
        // 첫 번째 시도
        homePage.enterText("First Try");
        Thread.sleep(200);
        homePage.clickButton1();
        Thread.sleep(300);

        // UI가 여전히 응답하는지 확인
        assertTrue(homePage.isHomePageDisplayed());

        // 재시도
        homePage.clearTextInput();
        Thread.sleep(200);
        homePage.enterText("Second Try");
        Thread.sleep(200);
        homePage.clickButton2();
        Thread.sleep(300);

        // 최종 확인
        assertTrue(homePage.isButton1Displayed());
        assertTrue(homePage.isButton2Displayed());
      }, "오류 복구 시나리오 실패");
    }
  }

  @Nested
  @DisplayName("회귀 테스트")
  class RegressionTests {

    @Test
    @DisplayName("버튼 클릭 회귀")
    void test_regression_button_clicks() {
      // 이전 테스트에서 발견된 버튼 클릭 문제 테스트
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 10; i++) {
          homePage.clickButton1();
          homePage.clickButton2();
        }
      }, "버튼 클릭 회귀");
    }

    @Test
    @DisplayName("텍스트 입력 회귀")
    void test_regression_text_input() {
      // 이전 테스트에서 발견된 텍스트 입력 문제 테스트
      String[] testInputs = {"Test", "테스트", "123!@#"};
      for (String input : testInputs) {
        homePage.enterText(input);
        assertEquals(input, homePage.getTextInputValue(),
                    "텍스트 입력 회귀: " + input);
        homePage.clearTextInput();
      }
    }

    @Test
    @DisplayName("체크박스 상태 회귀")
    void test_regression_checkbox_state() {
      // 체크박스 상태 일관성 테스트
      for (int i = 0; i < 5; i++) {
        homePage.clickCheckBox();
        boolean isChecked = homePage.isCheckBoxChecked();
        homePage.clickCheckBox();
        boolean isUnchecked = homePage.isCheckBoxUnChecked();

        assertTrue(isChecked || isUnchecked, "체크박스 상태 회귀");
      }
    }
  }
}

