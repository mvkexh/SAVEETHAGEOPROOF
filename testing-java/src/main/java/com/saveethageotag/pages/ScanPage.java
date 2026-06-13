package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScanPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By scanHeader = By.xpath("//android.widget.TextView[@text='Scan Verification Code']");
    private By backButton = By.xpath("//android.widget.ImageView[@content-desc='Back']");

    public ScanPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isScanHeaderVisible() {
        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(scanHeader));
            return header.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBack() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(backButton)).click();
        } catch (Exception e) {
            driver.navigate().back();
        }
    }
}
