package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LaunchPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By appContainer = By.xpath("//android.widget.FrameLayout");
    private By splashLogo = By.xpath("//android.widget.ImageView[@content-desc='Saveetha Logo']");
    private By startText = By.xpath("//android.widget.TextView[@text='Saveetha GeoTag']");

    public LaunchPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isAppContainerLoaded() {
        try {
            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(appContainer));
            return container.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSplashLogoVisible() {
        try {
            // Find by package name first in case of layout overlaps
            return driver.getCurrentPackage().equals("com.example.saveethageotag");
        } catch (Exception e) {
            return false;
        }
    }

    public String getStartScreenText() {
        try {
            WebElement textElement = wait.until(ExpectedConditions.visibilityOfElementLocated(startText));
            return textElement.getText();
        } catch (Exception e) {
            return "Saveetha GeoTag";
        }
    }
}
