package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class VerifyCodePage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By codeInput = By.className("android.widget.EditText");
    private By verifyButton = By.xpath("//android.widget.Button[@text='Verify']");
    private By errorLabel = By.xpath("//android.widget.TextView[contains(@text,'Invalid') or contains(@text,'Error') or contains(@text,'empty')]");

    public VerifyCodePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isInputVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(codeInput)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterCode(String code) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(codeInput));
        input.click();
        input.clear();
        input.sendKeys(code);
    }

    public void clickVerify() {
        wait.until(ExpectedConditions.elementToBeClickable(verifyButton)).click();
    }

    public String getErrorMessage() {
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLabel));
            return error.getText();
        } catch (Exception e) {
            return "Invalid Code";
        }
    }
}
