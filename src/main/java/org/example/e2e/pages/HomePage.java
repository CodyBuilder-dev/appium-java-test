package org.example.e2e.pages;

import org.example.e2e.utils.Waits;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class HomePage {
  private final AndroidDriver driver;

  // 홈 화면 레이아웃 요소
  private final By homeFrame = By.xpath("//android.widget.FrameLayout[@resource-id=\"android:id/content\"]");
  private final By headerTitle = By.xpath("//android.view.View[@content-desc=\"test app for automation\"]");

  // 버튼 요소들
  private final By button1 = By.xpath("//android.widget.Button[@content-desc=\"button_1\"]");
  private final By button2 = By.xpath("//android.widget.Button[@content-desc=\"button_2\"]");

  // EditText 요소
  private final By textInput = By.xpath("//android.widget.EditText[@hint=\"Enter text\"]");

  // CheckBox 요소
  private final By checkBox = By.xpath("//android.widget.CheckBox");
  private final By unCheckedMessage = By.xpath("//android.view.View[@content-desc=\"unchecked\"]");
  private final By checkedMessage = By.xpath("//android.view.View[@content-desc=\"check\"]");

  // Switch 요소
  private final By switchControl = By.xpath("//android.widget.Switch");

  // 하단 네비게이션 - content-desc에 줄바꿈 포함 ("home&#10;my" = "home\nmy")
  // 여러 XPath 옵션 (선택하기 좋은 것을 사용)
  private final By bottomNavigation = By.xpath("//android.view.View[contains(@content-desc, 'home') and contains(@content-desc, 'my')]");

  // 대체 XPath들 (하나가 작동하지 않으면 다른 것 사용)
  private final By bottomNavigationAlt1 = By.xpath("//android.view.ViewGroup[contains(@content-desc, 'home')]");
  private final By bottomNavigationAlt2 = By.xpath("//*[contains(@content-desc, 'home') and contains(@content-desc, 'my')]");
  private final By bottomNavigationAlt3 = By.xpath("//android.view.View[contains(normalize-space(@content-desc), 'home')]");

  // 홈 탭 (개별 요소)
  private final By homeTab = By.xpath("//android.widget.Button[@content-desc='home'] | //android.view.View[@content-desc='home'] | //*[@text='home']");

  // My 탭 (개별 요소)
  private final By myTab = By.xpath("//android.widget.Button[@content-desc='my'] | //android.view.View[@content-desc='my'] | //*[@text='my']");

  public HomePage(AndroidDriver driver) {
    this.driver = driver;
  }

  public void waitReady() {
    Waits.waitVisible(driver, homeFrame, 15);
  }

  public boolean isHomePageDisplayed() {
    try {
      return driver.findElement(homeFrame).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isHeaderTitleDisplayed() {
    try {
      return driver.findElement(headerTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  // 버튼 관련 메서드
  public void clickButton1() {
    driver.findElement(button1).click();
  }

  public void clickButton2() {
    driver.findElement(button2).click();
  }

  public boolean isButton1Displayed() {
    try {
      return driver.findElement(button1).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isButton2Displayed() {
    try {
      return driver.findElement(button2).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  // 텍스트 입력 관련 메서드
  public void enterText(String text) {
    driver.findElement(textInput).clear();
    driver.findElement(textInput).sendKeys(text);
  }

  public String getTextInputValue() {
    return driver.findElement(textInput).getText();
  }

  public void clearTextInput() {
    driver.findElement(textInput).clear();
  }

  public boolean isTextInputDisplayed() {
    try {
      return driver.findElement(textInput).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  // CheckBox 관련 메서드
  public void clickCheckBox() {
    driver.findElement(checkBox).click();
  }

  public boolean isCheckBoxDisplayed() {
    try {
      return driver.findElement(checkBox).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isCheckBoxChecked() {
    try {
      return driver.findElement(checkedMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isCheckBoxUnChecked() {
    try {
      return driver.findElement(unCheckedMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }



  // Switch 관련 메서드
  public void toggleSwitch() {
    driver.findElement(switchControl).click();
  }

  public boolean isSwitchDisplayed() {
    try {
      return driver.findElement(switchControl).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSwitchChecked() {
    try {
      return Boolean.parseBoolean(driver.findElement(switchControl).getAttribute("checked"));
    } catch (Exception e) {
      return false;
    }
  }

  // 네비게이션 관련 메서드
  public void clickBottomNavigation() {
    try {
      driver.findElement(bottomNavigation).click();
    } catch (Exception e) {
      // 기본 XPath 실패 시 대체 XPath 시도
      try {
        driver.findElement(bottomNavigationAlt1).click();
      } catch (Exception e2) {
        try {
          driver.findElement(bottomNavigationAlt2).click();
        } catch (Exception e3) {
          driver.findElement(bottomNavigationAlt3).click();
        }
      }
    }
  }

  public boolean isBottomNavigationDisplayed() {
    try {
      return driver.findElement(bottomNavigation).isDisplayed();
    } catch (Exception e) {
      // 대체 XPath 시도
      try {
        return driver.findElement(bottomNavigationAlt1).isDisplayed();
      } catch (Exception e2) {
        return false;
      }
    }
  }

  /**
   * 홈 탭 클릭
   * 하단 네비게이션에서 홈 탭을 클릭합니다
   */
  public void clickHomeTab() {
    try {
      driver.findElement(homeTab).click();
    } catch (Exception e) {
      // 개별 요소를 찾지 못하면 전체 네비게이션 클릭
      clickBottomNavigation();
    }
  }

  /**
   * My 탭 클릭
   * 하단 네비게이션에서 My 탭을 클릭합니다
   */
  public void clickMyTab() {
    try {
      driver.findElement(myTab).click();
    } catch (Exception e) {
      // 개별 요소를 찾지 못하면 전체 네비게이션 클릭
      clickBottomNavigation();
    }
  }

  /**
   * 홈 탭이 표시되는지 확인
   */
  public boolean isHomeTabDisplayed() {
    try {
      return driver.findElement(homeTab).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * My 탭이 표시되는지 확인
   */
  public boolean isMyTabDisplayed() {
    try {
      return driver.findElement(myTab).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}

