package org.example.e2e.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.HomePage;
import org.junit.jupiter.api.*;

/**
 * HomePage의 인터랙티브 요소들에 대한 종합 테스트
 *
 * 테스트하는 항목:
 * - 버튼 클릭 및 상태 변화
 * - 텍스트 입력 필드
 * - CheckBox 상태 관리
 * - Switch 토글
 * - 다중 사용자 시나리오
 */
@DisplayName("HomePage 인터랙션 테스트")
public class HomePageInteractionTest {

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
  @DisplayName("버튼 상호작용 테스트")
  class ButtonInteractionTests {

    @Test
    @DisplayName("Button 1 표시 확인")
    void test_button1_is_displayed() {
      boolean displayed = homePage.isButton1Displayed();
      assert displayed : "Button 1이 표시되지 않습니다";
    }

    @Test
    @DisplayName("Button 1 클릭")
    void test_click_button1() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(300);
      }, "Button 1 클릭에 실패했습니다");
    }

    @Test
    @DisplayName("Button 2 표시 확인")
    void test_button2_is_displayed() {
      boolean displayed = homePage.isButton2Displayed();
      assert displayed : "Button 2가 표시되지 않습니다";
    }

    @Test
    @DisplayName("Button 2 클릭")
    void test_click_button2() {
      assertDoesNotThrow(() -> {
        homePage.clickButton2();
        Thread.sleep(300);
      }, "Button 2 클릭에 실패했습니다");
    }

    @Test
    @DisplayName("연속 버튼 클릭")
    void test_multiple_button_clicks() {
      assertDoesNotThrow(() -> {
        homePage.clickButton1();
        Thread.sleep(200);
        homePage.clickButton2();
        Thread.sleep(200);
        homePage.clickButton1();
      }, "연속 버튼 클릭에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("텍스트 입력 테스트")
  class TextInputTests {

    @Test
    @DisplayName("텍스트 입력 필드 표시 확인")
    void test_text_input_is_displayed() {
      boolean displayed = homePage.isTextInputDisplayed();
      assert displayed : "텍스트 입력 필드가 표시되지 않습니다";
    }

    @Test
    @DisplayName("텍스트 입력")
    void test_enter_text() {
      String testText = "테스트 입력";
      assertDoesNotThrow(() -> homePage.enterText(testText), "텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("텍스트 입력 후 조회")
    void test_enter_and_verify_text() {
      String testText = "자동화 테스트";
      homePage.enterText(testText);
      String inputValue = homePage.getTextInputValue();
      assert inputValue.contains(testText) :
        "입력된 텍스트 '" + testText + "'를 확인할 수 없습니다. 실제: " + inputValue;
    }

    @Test
    @DisplayName("텍스트 입력 필드 초기화")
    void test_clear_text_input() {
      homePage.enterText("이 텍스트는 삭제될 것입니다");
      assertDoesNotThrow(() -> homePage.clearTextInput(), "텍스트 입력 필드 초기화에 실패했습니다");
    }

    @Test
    @DisplayName("긴 텍스트 입력")
    void test_long_text_input() {
      String longText = "This is a long text for testing the text input field with special characters: !@#$%^&*()_+-=[]{}|;':\",./<>?";
      assertDoesNotThrow(() -> homePage.enterText(longText), "긴 텍스트 입력에 실패했습니다");
    }

    @Test
    @DisplayName("숫자만 입력")
    void test_numeric_input() {
      String numericText = "1234567890";
      assertDoesNotThrow(() -> homePage.enterText(numericText), "숫자 입력에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("CheckBox 테스트")
  class CheckBoxTests {

    @Test
    @DisplayName("CheckBox 표시 확인")
    void test_checkbox_is_displayed() {
      boolean displayed = homePage.isCheckBoxDisplayed();
      assert displayed : "CheckBox가 표시되지 않습니다";
    }

    @Test
    @DisplayName("CheckBox 초기 상태 (미체크)")
    void test_checkbox_initial_state() {
      boolean checked = homePage.isCheckBoxChecked();
      assert !checked : "CheckBox의 초기 상태가 체크되어 있습니다";
    }

    @Test
    @DisplayName("CheckBox 체크")
    void test_check_checkbox() throws InterruptedException {
      homePage.clickCheckBox();
      Thread.sleep(300);
      boolean checked = homePage.isCheckBoxChecked();
      assert checked : "CheckBox 체크에 실패했습니다";
    }

    @Test
    @DisplayName("CheckBox 언체크")
    void test_uncheck_checkbox() throws InterruptedException {
      // 먼저 체크
      homePage.clickCheckBox();
      Thread.sleep(300);
      // 다시 언체크
      homePage.clickCheckBox();
      Thread.sleep(300);
      boolean checked = homePage.isCheckBoxChecked();
      assert !checked : "CheckBox 언체크에 실패했습니다";
    }

    @Test
    @DisplayName("CheckBox 토글 반복")
    void test_checkbox_toggle_multiple_times() throws InterruptedException {
      for (int i = 0; i < 3; i++) {
        homePage.clickCheckBox();
        Thread.sleep(200);
      }
      boolean checked = homePage.isCheckBoxChecked();
      assert checked : "CheckBox 3번 토글 후 체크 상태 확인 실패";
    }

    @Test
    @DisplayName("CheckBox 상태 메시지 확인")
    void test_checkbox_status_message() throws InterruptedException {
      // 체크 전
      boolean unCheckedVisible = homePage.isCheckBoxUnChecked();
      System.out.println("체크 전 'unchecked' 메시지 표시: " + unCheckedVisible);

      // 체크
      homePage.clickCheckBox();
      Thread.sleep(300);

      // 체크 후
      boolean checkedVisible = homePage.isCheckBoxUnChecked();
      System.out.println("체크 후 'checked' 메시지 표시: " + checkedVisible);

      assert (unCheckedVisible || checkedVisible) : "체크박스 상태 메시지가 표시되지 않습니다";
    }
  }

  @Nested
  @DisplayName("Switch 테스트")
  class SwitchTests {

    @Test
    @DisplayName("Switch 표시 확인")
    void test_switch_is_displayed() {
      boolean displayed = homePage.isSwitchDisplayed();
      assert displayed : "Switch가 표시되지 않습니다";
    }

    @Test
    @DisplayName("Switch 초기 상태")
    void test_switch_initial_state() {
      boolean checked = homePage.isSwitchChecked();
      System.out.println("Switch 초기 상태: " + (checked ? "ON" : "OFF"));
      // 초기 상태는 off여야 함
      assert !checked : "Switch의 초기 상태가 ON입니다";
    }

    @Test
    @DisplayName("Switch 토글")
    void test_toggle_switch() throws InterruptedException {
      boolean initialState = homePage.isSwitchChecked();
      homePage.toggleSwitch();
      Thread.sleep(300);
      boolean newState = homePage.isSwitchChecked();
      assert initialState != newState : "Switch 토글에 실패했습니다";
    }

    @Test
    @DisplayName("Switch 반복 토글")
    void test_switch_toggle_multiple_times() throws InterruptedException {
      for (int i = 0; i < 5; i++) {
        homePage.toggleSwitch();
        Thread.sleep(200);
      }
      System.out.println("✅ Switch 5번 토글 완료");
    }
  }

  @Nested
  @DisplayName("통합 사용자 시나리오 테스트")
  class UserScenarioTests {

    @Test
    @DisplayName("전체 폼 작성 시나리오")
    void test_complete_form_scenario() throws InterruptedException {
      // 1. 텍스트 입력
      homePage.enterText("사용자 이름");
      Thread.sleep(200);

      // 2. CheckBox 체크
      homePage.clickCheckBox();
      Thread.sleep(200);

      // 3. Switch 토글
      homePage.toggleSwitch();
      Thread.sleep(200);

      // 4. Button 클릭
      homePage.clickButton1();
      Thread.sleep(300);

      System.out.println("✅ 전체 폼 작성 시나리오 완료");
    }

    @Test
    @DisplayName("초기화 및 재입력 시나리오")
    void test_reset_and_reenter_scenario() throws InterruptedException {
      // 첫 번째 입력
      homePage.enterText("첫 번째 입력");
      Thread.sleep(200);

      // 초기화
      homePage.clearTextInput();
      Thread.sleep(200);

      // 두 번째 입력
      homePage.enterText("두 번째 입력");
      Thread.sleep(200);

      String finalValue = homePage.getTextInputValue();
      assert finalValue.contains("두 번째") : "재입력 값 확인 실패";
    }

    @Test
    @DisplayName("조건부 버튼 클릭 시나리오")
    void test_conditional_button_clicks() throws InterruptedException {
      // CheckBox 상태에 따라 다른 버튼 클릭
      boolean isChecked = homePage.isCheckBoxChecked();

      if (!isChecked) {
        homePage.clickCheckBox();
        Thread.sleep(200);
        homePage.clickButton1();
      } else {
        homePage.clickButton2();
      }

      System.out.println("✅ 조건부 버튼 클릭 완료");
    }
  }
}

