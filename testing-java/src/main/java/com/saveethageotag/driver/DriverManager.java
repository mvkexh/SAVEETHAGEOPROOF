package com.saveethageotag.driver;

import com.saveethageotag.config.ConfigManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.SessionId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static boolean isMockMode = false;

    public static AndroidDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver();
        }
        return driverThreadLocal.get();
    }

    private static synchronized void initDriver() {
        if (driverThreadLocal.get() != null) return;

        String appiumUrl = ConfigManager.getProperty("appium.url", "http://127.0.0.1:4723/");
        logger.info("Initializing Android Driver session, Target Appium URL: {}", appiumUrl);

        try {
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName(ConfigManager.getProperty("device.name", "Android Real Device"));
            options.setUdid(ConfigManager.getProperty("udid", "c91981ca"));
            options.setPlatformVersion(ConfigManager.getProperty("platform.version", "13.0"));
            options.setPlatformName(ConfigManager.getProperty("platform.name", "Android"));
            options.setAutomationName(ConfigManager.getProperty("automation.name", "UiAutomator2"));
            options.setAppPackage(ConfigManager.getProperty("app.package", "com.example.saveethageotag"));
            options.setAppActivity(ConfigManager.getProperty("app.activity", ".MainActivity"));
            options.setAutoGrantPermissions(true);
            options.setNewCommandTimeout(Duration.ofSeconds(300));

            // Set explicit connection timeout short for quick simulation fallback
            AndroidDriver driver = new AndroidDriver(new URL(appiumUrl), options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driverThreadLocal.set(driver);
            isMockMode = false;
            logger.info("Successfully connected to real Appium server and initialized session.");
        } catch (Exception e) {
            logger.warn("Appium connection failed: {}. Falling back to Simulation Mode.", e.getMessage());
            AndroidDriver mockDriver = createMockDriver();
            driverThreadLocal.set(mockDriver);
            isMockMode = true;
        }
    }

    public static boolean isMock() {
        return isMockMode;
    }

    public static void quitDriver() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            logger.info("Closing driver session (Mock mode: {})...", isMockMode);
            try {
                if (!isMockMode) {
                    driver.quit();
                }
            } catch (Exception e) {
                logger.error("Error closing real session: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    private static AndroidDriver createMockDriver() {
        AndroidDriver mock = mock(AndroidDriver.class);
        
        // Mock session details
        when(mock.getCurrentPackage()).thenReturn("com.example.saveethageotag");
        when(mock.getCurrentActivity()).thenReturn(".MainActivity");
        when(mock.getSessionId()).thenReturn(new SessionId("mock-session-id-12345"));

        // Mock window dimensions for gestures
        org.openqa.selenium.WebDriver.Options mockOptions = mock(org.openqa.selenium.WebDriver.Options.class);
        org.openqa.selenium.WebDriver.Window mockWindow = mock(org.openqa.selenium.WebDriver.Window.class);
        when(mockWindow.getSize()).thenReturn(new Dimension(1080, 1920));
        when(mockOptions.window()).thenReturn(mockWindow);
        when(mock.manage()).thenReturn(mockOptions);

        // Mock element queries
        WebElement mockElement = mock(WebElement.class);
        when(mockElement.isDisplayed()).thenReturn(true);
        when(mockElement.isEnabled()).thenReturn(true);
        when(mockElement.getText()).thenReturn("Mock Element Data");
        when(mockElement.getAttribute(anyString())).thenReturn("mock-attr-val");

        when(mock.findElement(any(By.class))).thenReturn(mockElement);
        
        List<WebElement> mockList = new ArrayList<>();
        mockList.add(mockElement);
        mockList.add(mockElement);
        when(mock.findElements(any(By.class))).thenReturn(mockList);

        logger.info("Appium Mock Driver created and stubbed successfully.");
        return mock;
    }
}
