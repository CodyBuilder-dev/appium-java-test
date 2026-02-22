package org.example.e2e.pages;

import org.example.e2e.utils.Waits;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LoginPage {
  private final AndroidDriver driver;

  // 예시 locator (Inspector로 실제 값 확인해서 교체)
  private final By idInput = By.xpath("//android.widget.EditText[@text='ID']");
  private final By pwInput = By.xpath("//android.widget.EditText[@text='Password']");
  private final By loginBtn = By.xpath("//android.widget.Button[@text='Login']");
  private final By errorMessage = By.xpath("//android.widget.TextView[@text='Login failed']");
  private final By signUpLink = By.xpath("//android.widget.TextView[@text='Sign up']");

  public LoginPage(AndroidDriver driver) {
    this.driver = driver;
  }

  public void waitReady() {
    Waits.waitVisible(driver, loginBtn, 15);
  }

  public void login(String id, String pw) {
    driver.findElement(idInput).sendKeys(id);
    driver.findElement(pwInput).sendKeys(pw);
    driver.findElement(loginBtn).click();
  }

  public void enterUsername(String username) {
    driver.findElement(idInput).sendKeys(username);
  }

  public void enterPassword(String password) {
    driver.findElement(pwInput).sendKeys(password);
  }

  public void clickLoginButton() {
    driver.findElement(loginBtn).click();
  }

  public void clickSignUpLink() {
    driver.findElement(signUpLink).click();
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return driver.findElement(errorMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoginButtonDisplayed() {
    try {
      return driver.findElement(loginBtn).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clearIdInput() {
    WebElement idElement = driver.findElement(idInput);
    idElement.clear();
  }

  public void clearPasswordInput() {
    WebElement pwElement = driver.findElement(pwInput);
    pwElement.clear();
  }
}