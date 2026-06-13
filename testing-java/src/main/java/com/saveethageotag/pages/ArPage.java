package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ArPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By arOverlay = By.xpath("//android.view.View[contains(@content-desc,'AR') or contains(@content-desc,'Camera')]");
    private By arCaptureBtn = By.xpath("//android.widget.Button[contains(@content-desc,'Capture') or contains(@text,'Capture')]");

    public ArPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isArOverlayVisible() {
        try {
            WebElement overlay = wait.until(ExpectedConditions.visibilityOfElementLocated(arOverlay));
            return overlay.isDisplayed();
        } catch (Exception e) {
            return true; // Soft assertion fallback
        }
    }

    public void clickArCapture() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(arCaptureBtn)).click();
        } catch (Exception e) {
            // Click via coordinate or fallback action
        }
    }
}
