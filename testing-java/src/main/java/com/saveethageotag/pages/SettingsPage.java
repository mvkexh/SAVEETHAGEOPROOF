package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SettingsPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By settingsTitle = By.xpath("//android.widget.TextView[@text='Settings']");
    private By darkModeSwitch = By.className("android.widget.Switch");
    private By aboutOption = By.xpath("//android.widget.TextView[@text='About']");
    private By helpOption = By.xpath("//android.widget.TextView[@text='Help Center']");
    private By privacyOption = By.xpath("//android.widget.TextView[@text='Privacy Policy']");
    private By termsOption = By.xpath("//android.widget.TextView[@text='Terms of Service']");

    public SettingsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isSettingsTitleVisible() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(settingsTitle));
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDarkModeSwitchVisible() {
        try {
            WebElement sw = wait.until(ExpectedConditions.visibilityOfElementLocated(darkModeSwitch));
            return sw.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateToAbout() {
        wait.until(ExpectedConditions.elementToBeClickable(aboutOption)).click();
    }

    public void navigateToHelp() {
        wait.until(ExpectedConditions.elementToBeClickable(helpOption)).click();
    }

    public void navigateToPrivacy() {
        wait.until(ExpectedConditions.elementToBeClickable(privacyOption)).click();
    }

    public void navigateToTerms() {
        wait.until(ExpectedConditions.elementToBeClickable(termsOption)).click();
    }
}
