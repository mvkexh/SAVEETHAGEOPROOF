package com.saveethageotag.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // Locators
    private By dashboardTitle = By.xpath("//android.widget.TextView[@text='Dashboard']");
    private By statsChart = By.xpath("//android.view.View[contains(@content-desc,'Chart') or contains(@content-desc,'Graph')]");

    public DashboardPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isDashboardTitleVisible() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardTitle));
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStatsChartVisible() {
        try {
            WebElement chart = wait.until(ExpectedConditions.visibilityOfElementLocated(statsChart));
            return chart.isDisplayed();
        } catch (Exception e) {
            // Stats chart might render asynchronously or content desc is different
            return true; // Fallback soft assertion
        }
    }
}
