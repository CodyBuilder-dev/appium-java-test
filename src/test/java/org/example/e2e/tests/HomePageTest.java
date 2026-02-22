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
 * HomePage 화면에 대한 포괄적인 테스트 클래스
 */
@DisplayName("홈페이지 테스트")
public class HomePageTest {

  private AndroidDriver driver;
  private HomePage homePage;

  @BeforeEach
  void setUp() {
    driver = DriverFactory.createAndroidDriver();
    homePage = new HomePage(driver);
    // 앱 실행 후 홈 화면 로드 대기
    homePage.waitReady();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Nested
  @DisplayName("홈페이지 기본 UI 테스트")
  class HomePageBasicUITests {

    @Test
    @DisplayName("홈페이지가 정상적으로 로드되는지 확인")
    void test_homepage_loads_correctly() {
      assertTrue(homePage.isHomePageDisplayed(), "홈페이지가 표시되지 않았습니다");
    }

    @Test
    @DisplayName("홈페이지 헤더 제목이 표시되는지 확인")
    void test_header_title_is_displayed() {
      assertTrue(homePage.isHeaderTitleDisplayed(), "헤더 제목이 표시되지 않았습니다");
    }

    @Test
    @DisplayName("버튼 1이 화면에 표시되는지 확인")
    void test_button1_is_displayed() {
      assertTrue(homePage.isButton1Displayed(), "버튼 1이 표시되지 않았습니다");
    }

    @Test
    @DisplayName("버튼 2가 화면에 표시되는지 확인")
    void test_button2_is_displayed() {
      assertTrue(homePage.isButton2Displayed(), "버튼 2가 표시되지 않았습니다");
    }

    @Test
    @DisplayName("텍스트 입력창이 화면에 표시되는지 확인")
    void test_text_input_is_displayed() {
      assertTrue(homePage.isTextInputDisplayed(), "텍스트 입력창이 표시되지 않았습니다");
    }

    @Test
    @DisplayName("체크박스가 화면에 표시되는지 확인")
    void test_checkbox_is_displayed() {
      // 체크박스가 표시되었는지 확인
      assertTrue(homePage.isCheckBoxUnChecked() || homePage.isCheckBoxChecked(),
                "체크박스가 표시되지 않았습니다");
    }

    @Test
    @DisplayName("스위치 요소가 화면에 표시되는지 확인")
    void test_switch_is_displayed() {
      assertTrue(homePage.isSwitchDisplayed(), "스위치가 표시되지 않았습니다");
    }

    @Test
    @DisplayName("하단 네비게이션이 화면에 표시되는지 확인")
    void test_bottom_navigation_is_displayed() {
      assertTrue(homePage.isBottomNavigationDisplayed(), "하단 네비게이션이 표시되지 않았습니다");
    }
  }

  @Nested
  @DisplayName("버튼 클릭 테스트")
  class ButtonClickTests {

    @Test
    @DisplayName("버튼 1 클릭 가능 여부 확인")
    void test_button1_is_clickable() {
      assertDoesNotThrow(() -> homePage.clickButton1(), "버튼 1이 클릭되지 않았습니다");
    }

    @Test
    @DisplayName("버튼 2 클릭 가능 여부 확인")
    void test_button2_is_clickable() {
      assertDoesNotThrow(() -> homePage.clickButton2(), "버튼 2가 클릭되지 않았습니다");
    }

    @Test
    @DisplayName("버튼 1과 버튼 2를 순차적으로 클릭")
    void test_click_both_buttons_sequentially() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        // 버튼 1 클릭 후 약간의 대기
        Thread.sleep(500);
        homePage.clickButton2();
      }, "버튼 클릭 중 오류가 발생했습니다");
    }

    @Test
    @DisplayName("버튼 1을 여러 번 클릭")
    void test_click_button1_multiple_times() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 3; i++) {
          homePage.clickButton1();
          Thread.sleep(300);
        }
      }, "버튼 1을 여러 번 클릭할 수 없습니다");
    }
  }

  @Nested
  @DisplayName("텍스트 입력 테스트")
  class TextInputTests {

    @Test
    @DisplayName("텍스트 입력 필드에 텍스트 입력")
    void test_enter_text_in_input_field() {
      String testText = "Hello Appium";
      homePage.enterText(testText);
      String inputValue = homePage.getTextInputValue();
      assertEquals(testText, inputValue, "입력한 텍스트가 저장되지 않았습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드 초기화")
    void test_clear_text_input() {
      homePage.enterText("Test");
      homePage.clearTextInput();
      String inputValue = homePage.getTextInputValue();
      assertTrue(inputValue.isEmpty(), "텍스트가 초기화되지 않았습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드에 긴 텍스트 입력")
    void test_enter_long_text() {
      String longText = "This is a long test text to verify if the input field can handle lengthy inputs without any issues";
      assertDoesNotThrow(() -> homePage.enterText(longText), "긴 텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드에 특수 문자 입력")
    void test_enter_special_characters() {
      String specialText = "!@#$%^&*()_+-=[]{}|;:',.<>?";
      assertDoesNotThrow(() -> homePage.enterText(specialText), "특수 문자 입력에 실패했습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드에 숫자만 입력")
    void test_enter_numbers_only() {
      String numberText = "123456789";
      homePage.enterText(numberText);
      String inputValue = homePage.getTextInputValue();
      assertEquals(numberText, inputValue, "숫자 입력이 저장되지 않았습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드에 반복적으로 텍스트 입력")
    void test_enter_text_repeatedly() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 3; i++) {
          homePage.enterText("Test" + i);
          Thread.sleep(200);
          homePage.clearTextInput();
          Thread.sleep(200);
        }
      }, "반복적인 텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("텍스트 입력 필드에 공백 입력")
    void test_enter_spaces() {
      String spaceText = "   ";
      homePage.enterText(spaceText);
      String inputValue = homePage.getTextInputValue();
      assertEquals(spaceText, inputValue, "공백 입력이 저장되지 않았습니다");
    }
  }

  @Nested
  @DisplayName("체크박스 테스트")
  class CheckBoxTests {

    @Test
    @DisplayName("체크박스의 초기 상태 확인")
    void test_checkbox_initial_state() {
      // 초기 상태는 체크 해제로 가정
      assertTrue(homePage.isCheckBoxUnChecked(), "체크박스가 처음부터 체크되어 있습니다");
    }

    @Test
    @DisplayName("체크박스 클릭하여 체크 상태로 변경")
    void test_checkbox_click_to_check() {
      if (homePage.isCheckBoxUnChecked()) {
        homePage.clickCheckBox();
        assertTrue(homePage.isCheckBoxChecked(), "체크박스가 체크되지 않았습니다");
      }
    }

    @Test
    @DisplayName("체크박스 클릭하여 체크 해제 상태로 변경")
    void test_checkbox_click_to_uncheck() {
      // 먼저 체크 상태로 변경
      if (homePage.isCheckBoxUnChecked()) {
        homePage.clickCheckBox();
      }
      // 그 다음 체크 해제
      homePage.clickCheckBox();
      assertTrue(homePage.isCheckBoxUnChecked(), "체크박스가 체크 해제되지 않았습니다");
    }

    @Test
    @DisplayName("체크박스를 여러 번 클릭")
    void test_checkbox_click_multiple_times() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 5; i++) {
          homePage.clickCheckBox();
          Thread.sleep(200);
        }
      }, "체크박스를 여러 번 클릭할 수 없습니다");
    }

    @Test
    @DisplayName("체크박스와 텍스트 입력의 동시 사용")
    void test_checkbox_and_text_input_together() {
      assertDoesNotThrow(() -> {
        homePage.enterText("Checkbox Test");
        homePage.clickCheckBox();
        assertTrue(homePage.isCheckBoxChecked(), "체크박스가 체크되지 않았습니다");
      }, "체크박스와 텍스트 입력의 동시 사용에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("스위치 테스트")
  class SwitchTests {

    @Test
    @DisplayName("스위치 토글 동작 확인")
    void test_switch_toggle() {
      assertDoesNotThrow(() -> homePage.toggleSwitch(), "스위치 토글이 실패했습니다");
    }

    @Test
    @DisplayName("스위치를 여러 번 토글")
    void test_switch_toggle_multiple_times() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 3; i++) {
          homePage.toggleSwitch();
          Thread.sleep(300);
        }
      }, "스위치를 여러 번 토글할 수 없습니다");
    }

    @Test
    @DisplayName("스위치와 버튼 동시 사용")
    void test_switch_and_button_together() {
      assertDoesNotThrow(() -> {
        homePage.toggleSwitch();
        Thread.sleep(200);
        homePage.clickButton1();
        Thread.sleep(200);
        homePage.toggleSwitch();
      }, "스위치와 버튼의 동시 사용에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("네비게이션 테스트")
  class NavigationTests {

    @Test
    @DisplayName("하단 네비게이션 클릭")
    void test_bottom_navigation_click() {
      assertDoesNotThrow(() -> homePage.clickBottomNavigation(), "하단 네비게이션 클릭에 실패했습니다");
    }

    @Test
    @DisplayName("하단 네비게이션을 여러 번 클릭")
    void test_bottom_navigation_click_multiple_times() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 3; i++) {
          homePage.clickBottomNavigation();
          Thread.sleep(300);
        }
      }, "하단 네비게이션을 여러 번 클릭할 수 없습니다");
    }
  }

  @Nested
  @DisplayName("복합 사용자 플로우 테스트")
  class UserFlowTests {

    @Test
    @DisplayName("홈페이지 모든 요소 순차적 사용")
    void test_complete_user_flow() {
      assertDoesNotThrow(() -> {
        // 1. 홈페이지가 로드되었는지 확인
        assertTrue(homePage.isHomePageDisplayed());

        // 2. 텍스트 입력
        homePage.enterText("Complete Flow Test");
        Thread.sleep(200);

        // 3. 버튼 클릭
        homePage.clickButton1();
        Thread.sleep(300);

        // 4. 체크박스 클릭
        homePage.clickCheckBox();
        Thread.sleep(200);

        // 5. 스위치 토글
        homePage.toggleSwitch();
        Thread.sleep(200);

        // 6. 버튼 2 클릭
        homePage.clickButton2();
        Thread.sleep(200);

        // 7. 텍스트 입력 초기화
        homePage.clearTextInput();
        Thread.sleep(100);

        // 8. 네비게이션 클릭
        homePage.clickBottomNavigation();
      }, "전체 사용자 플로우 중 오류가 발생했습니다");
    }

    @Test
    @DisplayName("텍스트 입력과 체크박스 조합 테스트")
    void test_text_and_checkbox_flow() {
      assertDoesNotThrow(() -> {
        String testText = "Test with Checkbox";
        homePage.enterText(testText);
        String inputValue = homePage.getTextInputValue();
        assertEquals(testText, inputValue);

        homePage.clickCheckBox();
        assertTrue(homePage.isCheckBoxChecked());

        homePage.enterText("Updated Text");
        assertEquals("Updated Text", homePage.getTextInputValue());
      }, "텍스트와 체크박스 조합 테스트에 실패했습니다");
    }

    @Test
    @DisplayName("모든 버튼과 토글 요소 테스트")
    void test_all_interactive_elements() {
      assertDoesNotThrow(() -> {
        // 모든 버튼 클릭
        homePage.clickButton1();
        Thread.sleep(100);
        homePage.clickButton2();
        Thread.sleep(100);

        // 모든 토글 요소
        homePage.clickCheckBox();
        Thread.sleep(100);
        homePage.toggleSwitch();
        Thread.sleep(100);

        // 네비게이션
        homePage.clickBottomNavigation();
      }, "모든 인터랙티브 요소 테스트에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("UI 안정성 테스트")
  class UIStabilityTests {

    @Test
    @DisplayName("빠른 연속 클릭 테스트")
    void test_rapid_clicks() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 10; i++) {
          homePage.clickButton1();
        }
      }, "빠른 연속 클릭에 실패했습니다");
    }

    @Test
    @DisplayName("롱 타임 텍스트 입력 테스트")
    void test_long_duration_text_input() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 5; i++) {
          homePage.enterText("Iteration " + i);
          Thread.sleep(500);
        }
      }, "장시간 텍스트 입력 테스트에 실패했습니다");
    }

    @Test
    @DisplayName("홈페이지 상태 일관성 확인")
    void test_homepage_consistency() {
      assertTrue(homePage.isHomePageDisplayed());
      homePage.clickButton1();
      assertTrue(homePage.isHomePageDisplayed(), "버튼 클릭 후에도 홈페이지가 표시되어야 합니다");

      homePage.enterText("Test");
      assertTrue(homePage.isHomePageDisplayed(), "텍스트 입력 후에도 홈페이지가 표시되어야 합니다");
    }
  }
}

