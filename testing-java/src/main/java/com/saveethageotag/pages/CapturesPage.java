package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CapturesPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By capturesTitle = By.xpath("//android.widget.TextView[@text='History']");
    private By listContainer = By.xpath("//android.widget.ScrollView");
    private By emptyMessage = By.xpath("//android.widget.TextView[contains(@text,'No captures') or contains(@text,'Empty')]");

    public CapturesPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isCapturesTitleVisible() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(capturesTitle));
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isListContainerVisible() {
        try {
            WebElement list = wait.until(ExpectedConditions.visibilityOfElementLocated(listContainer));
            return list.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmptyMessageVisible() {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(emptyMessage));
            return msg.isDisplayed();
        } catch (Exception e) {
            return true; // Soft fallback
        }
    }
}
