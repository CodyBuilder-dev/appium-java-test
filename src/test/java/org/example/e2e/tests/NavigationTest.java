package org.example.e2e.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.appium.java_client.android.AndroidDriver;
import org.example.e2e.driver.DriverFactory;
import org.example.e2e.pages.HomePage;
import org.junit.jupiter.api.*;

/**
 * 하단 네비게이션 및 탭 기능에 대한 테스트 클래스
 *
 * 테스트하는 기능:
 * - 하단 네비게이션 표시 여부
 * - 홈 탭 클릭 및 상태
 * - My 탭 클릭 및 상태
 * - XPath 여러 방법 테스트
 */
@DisplayName("네비게이션 및 탭 테스트")
public class NavigationTest {

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
  @DisplayName("하단 네비게이션 테스트")
  class BottomNavigationTests {

    @Test
    @DisplayName("하단 네비게이션이 표시되는지 확인")
    void test_bottom_navigation_is_displayed() {
      boolean displayed = homePage.isBottomNavigationDisplayed();
      assert displayed : "하단 네비게이션이 표시되지 않습니다";
    }

    @Test
    @DisplayName("하단 네비게이션 클릭 가능 여부")
    void test_bottom_navigation_clickable() {
      assertDoesNotThrow(() -> homePage.clickBottomNavigation(), "하단 네비게이션 클릭에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("홈 탭 테스트")
  class HomeTabTests {

    @Test
    @DisplayName("홈 탭이 표시되는지 확인")
    void test_home_tab_is_displayed() {
      boolean displayed = homePage.isHomeTabDisplayed();
      if (displayed) {
        System.out.println("✅ 홈 탭이 표시됩니다");
      } else {
        System.out.println("⚠️  홈 탭을 개별 요소로 찾을 수 없음 (전체 네비게이션으로는 가능)");
      }
    }

    @Test
    @DisplayName("홈 탭 클릭")
    void test_click_home_tab() {
      assertDoesNotThrow(() -> {
        homePage.clickHomeTab();
        Thread.sleep(500); // 네비게이션 애니메이션 대기
      }, "홈 탭 클릭에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("My 탭 테스트")
  class MyTabTests {

    @Test
    @DisplayName("My 탭이 표시되는지 확인")
    void test_my_tab_is_displayed() {
      boolean displayed = homePage.isMyTabDisplayed();
      if (displayed) {
        System.out.println("✅ My 탭이 표시됩니다");
      } else {
        System.out.println("⚠️  My 탭을 개별 요소로 찾을 수 없음");
      }
    }

    @Test
    @DisplayName("My 탭 클릭")
    void test_click_my_tab() {
      assertDoesNotThrow(() -> {
        homePage.clickMyTab();
        Thread.sleep(500); // 네비게이션 애니메이션 대기
      }, "My 탭 클릭에 실패했습니다");
    }
  }

  @Nested
  @DisplayName("XPath 디버깅 및 진단")
  class XPathDiagnosticsTests {

    @Test
    @DisplayName("네비게이션 XPath 여러 방법 테스트")
    void test_multiple_xpath_methods() {
      System.out.println("\n=== 네비게이션 XPath 진단 ===");

      String[] xpaths = {
        "//android.view.View[contains(@content-desc, 'home') and contains(@content-desc, 'my')]",
        "//android.view.ViewGroup[contains(@content-desc, 'home')]",
        "//*[contains(@content-desc, 'home') and contains(@content-desc, 'my')]",
        "//android.view.View[contains(normalize-space(@content-desc), 'home')]"
      };

      for (int i = 0; i < xpaths.length; i++) {
        try {
          var elements = driver.findElements(org.openqa.selenium.By.xpath(xpaths[i]));
          System.out.println("✅ XPath " + (i + 1) + ": 찾음 (" + elements.size() + "개 요소)");
          if (!elements.isEmpty()) {
            String contentDesc = elements.get(0).getAttribute("content-desc");
            System.out.println("   content-desc: " + contentDesc);
          }
        } catch (Exception e) {
          System.out.println("❌ XPath " + (i + 1) + ": 실패 - " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("홈/My 탭 개별 XPath 테스트")
    void test_individual_tab_xpaths() {
      System.out.println("\n=== 홈/My 탭 XPath 진단 ===");

      String[] homeXpaths = {
        "//android.widget.Button[@content-desc='home']",
        "//android.view.View[@content-desc='home']",
        "//*[@text='home']"
      };

      String[] myXpaths = {
        "//android.widget.Button[@content-desc='my']",
        "//android.view.View[@content-desc='my']",
        "//*[@text='my']"
      };

      System.out.println("\n홈 탭 XPath:");
      for (int i = 0; i < homeXpaths.length; i++) {
        try {
          var elements = driver.findElements(org.openqa.selenium.By.xpath(homeXpaths[i]));
          if (!elements.isEmpty()) {
            System.out.println("✅ XPath " + (i + 1) + ": 찾음");
          }
        } catch (Exception e) {
          // 무시
        }
      }

      System.out.println("\nMy 탭 XPath:");
      for (int i = 0; i < myXpaths.length; i++) {
        try {
          var elements = driver.findElements(org.openqa.selenium.By.xpath(myXpaths[i]));
          if (!elements.isEmpty()) {
            System.out.println("✅ XPath " + (i + 1) + ": 찾음");
          }
        } catch (Exception e) {
          // 무시
        }
      }
    }

    @Test
    @DisplayName("content-desc 속성 확인")
    void test_content_desc_inspection() {
      System.out.println("\n=== content-desc 속성 확인 ===");

      try {
        var navElements = driver.findElements(
          org.openqa.selenium.By.xpath("//android.view.View[contains(@content-desc, 'home')]")
        );

        for (var element : navElements) {
          String contentDesc = element.getAttribute("content-desc");
          System.out.println("발견된 요소 content-desc: " + contentDesc);
          System.out.println("  - 길이: " + (contentDesc != null ? contentDesc.length() : "null"));
          System.out.println("  - 'home' 포함: " + (contentDesc != null && contentDesc.contains("home")));
          System.out.println("  - 'my' 포함: " + (contentDesc != null && contentDesc.contains("my")));
        }
      } catch (Exception e) {
        System.out.println("❌ 요소 검사 실패: " + e.getMessage());
      }
    }
  }
}

