package org.example.e2e.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Waits {
  public static void waitVisible(WebDriver driver, By by, int sec) {
    new WebDriverWait(driver, Duration.ofSeconds(sec))
        .until(ExpectedConditions.visibilityOfElementLocated(by));
  }
}
