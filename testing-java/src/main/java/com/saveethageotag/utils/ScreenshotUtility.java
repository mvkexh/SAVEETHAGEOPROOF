package com.saveethageotag.utils;

import com.saveethageotag.config.ConfigManager;
import com.saveethageotag.driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ScreenshotUtility {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtility.class);

    public static String captureScreenshot(String testName) {
        String dir = ConfigManager.getProperty("reports.screenshots.dir", "screenshots");
        String path = dir + "/" + testName + "_" + System.currentTimeMillis() + ".png";

        try {
            if (DriverManager.isMock()) {
                // Return a dummy empty PNG representation or create a mock 1x1 image file
                File dest = new File(path);
                // Tiny transparent 1x1 png in base64
                String base64Png = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
                byte[] decoded = Base64.getDecoder().decode(base64Png);
                FileUtils.writeByteArrayToFile(dest, decoded);
                logger.info("Mock screenshot saved at: {}", path);
                return dest.getAbsolutePath();
            }

            File src = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            File dest = new File(path);
            FileUtils.copyFile(src, dest);
            logger.info("Screenshot saved successfully at: {}", path);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }
}
