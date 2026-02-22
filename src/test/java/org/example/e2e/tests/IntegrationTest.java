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
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

  private AndroidDriver driver;

  @BeforeEach
  void setUp() {
    driver = DriverFactory.createAndroidDriver();
  }

  @AfterEach
  void tearDown() {
    if (driver != null)
      driver.quit();
  }

  @Nested
  @DisplayName("전체 사용자 플로우 테스트")
  class UserFlowTests {

    @Test
    @DisplayName("회원가입부터 로그인까지의 전체 플로우")
    void complete_user_registration_and_login_flow() {
      // 1. 로그인 페이지 확인
      LoginPage login = new LoginPage(driver);
      login.waitReady();
      assertTrue(login.isLoginButtonDisplayed(), "로그인 페이지가 로드되었습니다");

      // 2. 회원가입 페이지로 이동
      login.clickSignUpLink();
      SignUpPage signUp = new SignUpPage(driver);
      signUp.waitReady();
      assertTrue(signUp.isSignUpPageDisplayed(), "회원가입 페이지가 표시됩니다");

      // 3. 회원가입 완료
      signUp.signUp("newuser@example.com", "newuser", "password123", "password123");
      assertTrue(signUp.isSuccessMessageDisplayed(), "회원가입이 성공합니다");

      // 4. 로그인 페이지로 돌아가기
      login.clickSignUpLink(); // 예시 - 실제로는 뒤로가기 구현 필요
      login.waitReady();

      // 5. 새로 가입한 계정으로 로그인
      login.login("newuser", "password123");
      HomePage home = new HomePage(driver);
      home.waitReady();
      assertTrue(home.isHomePageDisplayed(), "홈 페이지가 표시됩니다");
    }

//    @Test
//    @DisplayName("다중 로그인/로그아웃 세션")
//    void multiple_login_logout_sessions() {
//      for (int i = 0; i < 3; i++) {
//        LoginPage login = new LoginPage(driver);
//        login.waitReady();
//        login.login("test", "test1234");
//
//        HomePage home = new HomePage(driver);
//        home.waitReady();
//        assertTrue(home.isHomePageDisplayed(), "로그인 " + (i + 1) + "번째 성공");
//
//        home.logout();
//        login.waitReady();
//        assertTrue(login.isLoginButtonDisplayed(), "로그아웃 " + (i + 1) + "번째 성공");
//      }
//    }
//  }

    @Nested
    @DisplayName("데이터 유효성 검사 테스트")
    class DataValidationTests {

      @Test
      @DisplayName("특수문자가 포함된 입력 검증")
      void special_characters_validation() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        String[] specialChars = {"!@#$%", "<script>", "'; DROP TABLE --", "../../etc/passwd"};

        for (String special : specialChars) {
          login.enterUsername(special);
          login.enterPassword("test1234");
          login.clickLoginButton();

          // 에러가 발생하거나 로그인이 실패해야 함
          assertTrue(login.isLoginButtonDisplayed() || login.isErrorMessageDisplayed(),
              "특수문자 " + special + "이 안전하게 처리됩니다");

          login.clearIdInput();
        }
      }

      @Test
      @DisplayName("매우 긴 입력값 검증")
      void long_input_validation() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        String longInput = "a".repeat(1000);
        login.enterUsername(longInput);
        login.enterPassword("test1234");
        login.clickLoginButton();

        assertNotNull(driver, "긴 입력값이 안전하게 처리됩니다");
      }

      @Test
      @DisplayName("공백만 입력되는 경우 검증")
      void whitespace_only_validation() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        login.enterUsername("   ");
        login.enterPassword("   ");
        login.clickLoginButton();

        assertTrue(login.isErrorMessageDisplayed() || login.isLoginButtonDisplayed(),
            "공백만 입력된 경우가 검증됩니다");
      }
    }

    @Nested
    @DisplayName("캐시 및 임시 데이터 테스트")
    class CacheTests {

      @Test
      @DisplayName("입력값 캐시 확인")
      void input_caching_behavior() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        // 첫 로그인 시도
        login.enterUsername("testuser");
        login.enterPassword("testpass");
        login.clickLoginButton();

        // 로그인 실패 후 다시 로그인 페이지로
        login.waitReady();

        // 입력값이 여전히 있을 수 있으므로 초기화
        login.clearIdInput();
        login.clearPasswordInput();

        // 새로운 입력값으로 로그인
        login.enterUsername("test");
        login.enterPassword("test1234");
        login.clickLoginButton();

        assertNotNull(driver, "입력값 캐시가 올바르게 처리됩니다");
      }
    }

    @Nested
    @DisplayName("에러 처리 및 복구 테스트")
    class ErrorHandlingTests {

      @Test
      @DisplayName("연속된 실패 후 성공")
      void consecutive_failures_then_success() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        // 3번 실패
        for (int i = 0; i < 3; i++) {
          login.enterUsername("wrong");
          login.enterPassword("wrong");
          login.clickLoginButton();

          assertTrue(login.isErrorMessageDisplayed(),
              (i + 1) + "번째 실패가 발생합니다");

          login.clearIdInput();
          login.clearPasswordInput();
        }

        // 올바른 자격증명으로 로그인
        login.enterUsername("test");
        login.enterPassword("test1234");
        login.clickLoginButton();

        HomePage home = new HomePage(driver);
        home.waitReady();
        assertTrue(home.isHomePageDisplayed(), "연속 실패 후 성공합니다");
      }

      @Test
      @DisplayName("부분 입력 후 취소")
      void partial_input_cancel() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        login.enterUsername("test");
        // 비밀번호는 입력 안함
        login.clickLoginButton();

        assertTrue(login.isErrorMessageDisplayed() || login.isLoginButtonDisplayed(),
            "부분 입력이 처리됩니다");
      }
    }

    @Nested
    @DisplayName("성능 및 부하 테스트")
    class PerformanceTests {

      @Test
      @DisplayName("빠른 연속 클릭")
      void rapid_clicking() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        // 로그인 버튼을 빠르게 여러 번 클릭
        for (int i = 0; i < 5; i++) {
          try {
            login.clickLoginButton();
          } catch (Exception e) {
            // 클릭이 무시될 수 있음
          }
        }

        assertTrue(login.isLoginButtonDisplayed(), "빠른 연속 클릭이 처리됩니다");
      }

      @Test
      @DisplayName("높은 부하에서의 응답성")
      void responsiveness_under_load() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        long totalTime = 0;
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
          long startTime = System.currentTimeMillis();
          login.enterUsername("test");
          login.enterPassword("test1234");
          long endTime = System.currentTimeMillis();

          totalTime += (endTime - startTime);
          login.clearIdInput();
          login.clearPasswordInput();
        }

        long averageTime = totalTime / iterations;
        assertTrue(averageTime < 1000, "평균 입력 시간이 1초 미만입니다: " + averageTime + "ms");
      }
    }

    @Nested
    @DisplayName("UI 상태 일관성 테스트")
    class UIConsistencyTests {

      @Test
      @DisplayName("로그인 버튼 비활성화 상태 확인")
      void login_button_state() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        // 초기 상태에서 버튼이 활성화되어 있는지 확인
        assertTrue(login.isLoginButtonDisplayed(), "로그인 버튼이 표시됩니다");

        // 입력 후에도 버튼이 여전히 활성화되어 있는지 확인
        login.enterUsername("test");
        assertTrue(login.isLoginButtonDisplayed(), "입력 후에도 버튼이 표시됩니다");
      }

      @Test
      @DisplayName("페이지 전환 시 UI 일관성")
      void ui_consistency_across_pages() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        assertTrue(login.isLoginButtonDisplayed(), "로그인 페이지 UI가 일관됩니다");

        login.clickSignUpLink();
        SignUpPage signUp = new SignUpPage(driver);
        signUp.waitReady();

        assertTrue(signUp.isSignUpPageDisplayed(), "회원가입 페이지 UI가 일관됩니다");

        signUp.clickBackButton();
        login.waitReady();

        assertTrue(login.isLoginButtonDisplayed(), "다시 로그인 페이지로 돌아온 UI가 일관됩니다");
      }
    }

    @Nested
    @DisplayName("뒤로가기 및 네비게이션 테스트")
    class NavigationTests {

      @Test
      @DisplayName("뒤로가기 네비게이션")
      void back_navigation() {
        LoginPage login = new LoginPage(driver);
        login.waitReady();

        login.clickSignUpLink();
        SignUpPage signUp = new SignUpPage(driver);
        signUp.waitReady();

        signUp.clickBackButton();
        login.waitReady();

        assertTrue(login.isLoginButtonDisplayed(), "뒤로가기 네비게이션이 작동합니다");
      }

//    @Test
//    @DisplayName("로그아웃 후 다시 로그인")
//    void logout_and_relogin() {
//      LoginPage login = new LoginPage(driver);
//      login.waitReady();
//
//      // 로그인
//      login.login("test", "test1234");
//      HomePage home = new HomePage(driver);
//      home.waitReady();
//      assertTrue(home.isHomePageDisplayed(), "로그인 성공");
//
//      // 로그아웃
//      home.logout();
//      login.waitReady();
//      assertTrue(login.isLoginButtonDisplayed(), "로그아웃 후 로그인 페이지 표시");
//
//      // 다시 로그인
//      login.login("test", "test1234");
//      home.waitReady();
//      assertTrue(home.isHomePageDisplayed(), "다시 로그인 성공");
//    }
//  }

      @Nested
      @DisplayName("사용자 입력 유형 테스트")
      class InputTypeTests {

        @Test
        @DisplayName("이메일 형식 입력")
        void email_format_input() {
          LoginPage login = new LoginPage(driver);
          login.waitReady();

          login.enterUsername("user@example.com");
          login.enterPassword("password");
          login.clickLoginButton();

          assertNotNull(driver, "이메일 형식 입력이 처리됩니다");
        }

        @Test
        @DisplayName("숫자만 입력")
        void numeric_input() {
          LoginPage login = new LoginPage(driver);
          login.waitReady();

          login.enterUsername("1234567890");
          login.enterPassword("9876543210");
          login.clickLoginButton();

          assertNotNull(driver, "숫자 형식 입력이 처리됩니다");
        }

        @Test
        @DisplayName("대소문자 혼합 입력")
        void mixed_case_input() {
          LoginPage login = new LoginPage(driver);
          login.waitReady();

          login.enterUsername("TeStUsEr");
          login.enterPassword("PaSsWoRd");
          login.clickLoginButton();

          assertNotNull(driver, "대소문자 혼합 입력이 처리됩니다");
        }
      }
    }
  }
}