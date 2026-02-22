package org.example.e2e.pages;

import org.example.e2e.utils.Waits;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class SignUpPage {
  private final AndroidDriver driver;

  // 예시 locator (실제 앱의 회원가입 화면 요소로 교체)
  private final By signUpTitle = By.xpath("//android.widget.TextView[@text='Sign Up']");
  private final By emailInput = By.xpath("//android.widget.EditText[@text='Email']");
  private final By usernameInput = By.xpath("//android.widget.EditText[@text='Username']");
  private final By passwordInput = By.xpath("//android.widget.EditText[@text='Password']");
  private final By confirmPasswordInput = By.xpath("//android.widget.EditText[@text='Confirm Password']");
  private final By signUpBtn = By.xpath("//android.widget.Button[@text='Sign Up']");
  private final By backBtn = By.xpath("//android.widget.Button[@content-desc='back']");
  private final By successMessage = By.xpath("//android.widget.TextView[@text='Account created successfully']");

  public SignUpPage(AndroidDriver driver) {
    this.driver = driver;
  }

  public void waitReady() {
    Waits.waitVisible(driver, signUpTitle, 15);
  }

  public boolean isSignUpPageDisplayed() {
    try {
      return driver.findElement(signUpTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void enterEmail(String email) {
    driver.findElement(emailInput).sendKeys(email);
  }

  public void enterUsername(String username) {
    driver.findElement(usernameInput).sendKeys(username);
  }

  public void enterPassword(String password) {
    driver.findElement(passwordInput).sendKeys(password);
  }

  public void enterConfirmPassword(String confirmPassword) {
    driver.findElement(confirmPasswordInput).sendKeys(confirmPassword);
  }

  public void clickSignUpButton() {
    driver.findElement(signUpBtn).click();
  }

  public void clickBackButton() {
    driver.findElement(backBtn).click();
  }

  public boolean isSuccessMessageDisplayed() {
    try {
      return driver.findElement(successMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void signUp(String email, String username, String password, String confirmPassword) {
    enterEmail(email);
    enterUsername(username);
    enterPassword(password);
    enterConfirmPassword(confirmPassword);
    clickSignUpButton();
  }
}

