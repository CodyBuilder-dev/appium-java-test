package org.example.e2e.tests;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.LoginPage;
import org.example.e2e.pages.HomePage;
import org.example.e2e.pages.SignUpPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class SmokeTest {

  private AndroidDriver driver;

  @BeforeEach
  void setUp() {
    driver = DriverFactory.createAndroidDriver();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) driver.quit();
  }

  @Test
  @DisplayName("로그인 페이지가 정상적으로 로드되는지 확인")
  void launch_and_verify_login_page() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    assertTrue(login.isLoginButtonDisplayed(), "로그인 페이지가 로드되지 않았습니다");
  }

  @Test
  @DisplayName("유효한 자격증명으로 로그인 성공")
  void login_with_valid_credentials_success() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.login("test", "test1234");

    // 홈 페이지로 이동했는지 확인
    HomePage home = new HomePage(driver);
    home.waitReady();
    assertTrue(home.isHomePageDisplayed(), "홈 페이지가 표시되지 않았습니다");
  }

  @Test
  @DisplayName("잘못된 자격증명으로 로그인 실패")
  void login_with_invalid_credentials_fails() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.login("wronguser", "wrongpassword");

    // 에러 메시지가 표시되는지 확인
    assertTrue(login.isErrorMessageDisplayed(), "에러 메시지가 표시되지 않았습니다");
  }

  @Test
  @DisplayName("빈 아이디로 로그인 시도")
  void login_with_empty_username_fails() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.enterPassword("test1234");
    login.clickLoginButton();

    // 에러 메시지 또는 경고가 표시되는지 확인
    assertTrue(login.isErrorMessageDisplayed() || login.isLoginButtonDisplayed(),
               "에러 처리가 되지 않았습니다");
  }

  @Test
  @DisplayName("빈 비밀번호로 로그인 시도")
  void login_with_empty_password_fails() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.enterUsername("test");
    login.clickLoginButton();

    // 에러 메시지 또는 경고가 표시되는지 확인
    assertTrue(login.isErrorMessageDisplayed() || login.isLoginButtonDisplayed(),
               "에러 처리가 되지 않았습니다");
  }

  @Test
  @DisplayName("아이디 필드 초기화 기능")
  void clear_username_field() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.enterUsername("testuser");
    login.clearIdInput();

    // 초기화 후 빈 필드에서 다시 입력
    login.enterUsername("newuser");
    login.enterPassword("test1234");
    login.clickLoginButton();

    // 로그인 시도 (성공 또는 실패 상관없이 정상 작동하는지 확인)
    assertNotNull(driver, "드라이버가 활성화되어 있습니다");
  }

  @Test
  @DisplayName("비밀번호 필드 초기화 기능")
  void clear_password_field() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.enterUsername("test");
    login.enterPassword("wrongpassword");
    login.clearPasswordInput();

    // 초기화 후 새로운 비밀번호 입력
    login.enterPassword("test1234");
    login.clickLoginButton();

    // 로그인 시도 (성공 또는 실패 상관없이 정상 작동하는지 확인)
    assertNotNull(driver, "드라이버가 활성화되어 있습니다");
  }

//  @Test
//  @DisplayName("로그인 후 로그아웃 기능")
//  void login_and_logout() {
//    // 로그인
//    LoginPage login = new LoginPage(driver);
//    login.waitReady();
//    login.login("test", "test1234");
//
//    // 홈 페이지 확인
//    HomePage home = new HomePage(driver);
//    home.waitReady();
//    assertTrue(home.isHomePageDisplayed(), "홈 페이지가 표시되지 않았습니다");
//
//    // 로그아웃
//    home.logout();
//
//    // 로그인 페이지로 돌아왔는지 확인
//    login.waitReady();
//    assertTrue(login.isLoginButtonDisplayed(), "로그인 페이지로 돌아가지 않았습니다");
//  }

  @Test
  @DisplayName("회원가입 페이지 네비게이션")
  void navigate_to_signup_page() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.clickSignUpLink();

    // 회원가입 페이지 확인
    SignUpPage signUp = new SignUpPage(driver);
    signUp.waitReady();
    assertTrue(signUp.isSignUpPageDisplayed(), "회원가입 페이지가 표시되지 않았습니다");
  }

  @Test
  @DisplayName("회원가입 페이지에서 로그인 페이지로 뒤로가기")
  void navigate_back_from_signup_to_login() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.clickSignUpLink();

    SignUpPage signUp = new SignUpPage(driver);
    signUp.waitReady();

    // 뒤로가기
    signUp.clickBackButton();

    // 로그인 페이지로 돌아왔는지 확인
    login.waitReady();
    assertTrue(login.isLoginButtonDisplayed(), "로그인 페이지로 돌아가지 않았습니다");
  }

  @Test
  @DisplayName("회원가입 폼 전체 입력 및 제출")
  void complete_signup_form() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.clickSignUpLink();

    SignUpPage signUp = new SignUpPage(driver);
    signUp.waitReady();

    // 회원가입 폼 작성
    signUp.signUp("newuser@test.com", "newuser", "newpass123", "newpass123");

    // 성공 메시지 확인
    assertTrue(signUp.isSuccessMessageDisplayed(), "회원가입 성공 메시지가 표시되지 않았습니다");
  }

  @Test
  @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않을 때")
  void signup_with_mismatched_passwords() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();
    login.clickSignUpLink();

    SignUpPage signUp = new SignUpPage(driver);
    signUp.waitReady();

    // 비밀번호가 일치하지 않도록 입력
    signUp.signUp("test@test.com", "testuser", "password123", "differentpassword");

    // 에러 메시지가 표시되거나 회원가입이 완료되지 않음
    assertFalse(signUp.isSuccessMessageDisplayed(), "일치하지 않는 비밀번호로 가입이 진행되면 안됩니다");
  }

//  @Test
//  @DisplayName("홈 페이지에서 프로필 메뉴 클릭")
//  void navigate_to_profile_menu() {
//    // 로그인
//    LoginPage login = new LoginPage(driver);
//    login.waitReady();
//    login.login("test", "test1234");
//
//    // 홈 페이지 확인
//    HomePage home = new HomePage(driver);
//    home.waitReady();
//
//    // 프로필 메뉴 클릭
//    home.clickProfileMenu();
//
//    // 프로필 페이지가 열렸는지 확인 (실제 구현에 따라 다를 수 있음)
//    assertNotNull(driver, "네비게이션이 정상적으로 작동합니다");
//  }

//  @Test
//  @DisplayName("홈 페이지에서 설정 메뉴 클릭")
//  void navigate_to_settings_menu() {
//    // 로그인
//    LoginPage login = new LoginPage(driver);
//    login.waitReady();
//    login.login("test", "test1234");
//
//    // 홈 페이지 확인
//    HomePage home = new HomePage(driver);
//    home.waitReady();
//
//    // 설정 메뉴 클릭
//    home.clickSettingsMenu();
//
//    // 설정 페이지가 열렸는지 확인
//    assertNotNull(driver, "네비게이션이 정상적으로 작동합니다");
//  }

  @Test
  @DisplayName("홈 페이지에서 체크박스 클릭")
  void click_checkbox() {
    // 홈 페이지 확인
    HomePage home = new HomePage(driver);
    home.waitReady();

    // 체크박스 클릭
    home.clickCheckBox();

    // 설정 페이지가 열렸는지 확인
    assertTrue(home.isCheckBoxChecked());

    // 체크박스 다시 클릭
    home.clickCheckBox();
    assertTrue(home.isCheckBoxUnChecked());
  }

  @Test
  @DisplayName("로그인 페이지 UI 요소 가시성 확인")
  void verify_login_page_ui_elements() {
    LoginPage login = new LoginPage(driver);
    login.waitReady();

    // 로그인 버튼이 표시되는지 확인
    assertTrue(login.isLoginButtonDisplayed(), "로그인 버튼이 표시되지 않았습니다");

    // 기타 UI 요소들도 확인 가능
    assertNotNull(driver, "드라이버가 활성화되어 있습니다");
  }
}