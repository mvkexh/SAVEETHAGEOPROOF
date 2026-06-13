package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By bottomNav = By.xpath("//android.widget.LinearLayout");
    private By captureTab = By.xpath("//android.widget.TextView[@text='Capture']");
    private By statsTab = By.xpath("//android.widget.TextView[@text='Stats']");
    private By verifyTab = By.xpath("//android.widget.TextView[@text='Verify']");
    private By scanTab = By.xpath("//android.widget.TextView[@text='Scan']");
    private By historyTab = By.xpath("//android.widget.TextView[@text='History']");
    private By settingsTab = By.xpath("//android.widget.TextView[@text='Settings']");

    public HomePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isBottomNavVisible() {
        try {
            WebElement nav = wait.until(ExpectedConditions.visibilityOfElementLocated(bottomNav));
            return nav.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateToCapture() {
        wait.until(ExpectedConditions.elementToBeClickable(captureTab)).click();
    }

    public void navigateToStats() {
        wait.until(ExpectedConditions.elementToBeClickable(statsTab)).click();
    }

    public void navigateToVerify() {
        wait.until(ExpectedConditions.elementToBeClickable(verifyTab)).click();
    }

    public void navigateToScan() {
        wait.until(ExpectedConditions.elementToBeClickable(scanTab)).click();
    }

    public void navigateToHistory() {
        wait.until(ExpectedConditions.elementToBeClickable(historyTab)).click();
    }

    public void navigateToSettings() {
        wait.until(ExpectedConditions.elementToBeClickable(settingsTab)).click();
    }
}
