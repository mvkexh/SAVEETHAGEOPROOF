package com.saveethageotag.listeners;

import com.saveethageotag.annotations.TestCaseInfo;
import com.saveethageotag.utils.ReportManager;
import com.saveethageotag.utils.ScreenshotUtility;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        TestCaseInfo info = method.getAnnotation(TestCaseInfo.class);

        String tcId = info != null ? info.id() : "TC_GEN";
        String category = info != null ? info.category() : "General Testing";
        String screen = info != null ? info.screen() : "Home Screen";
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = result.getName();
        }

        logger.info("Executing Test: {} - {} [Category: {}, Screen: {}]", tcId, description, category, screen);
        ReportManager.startTest(tcId, description, category, screen);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        TestCaseInfo info = method.getAnnotation(TestCaseInfo.class);

        String tcId = info != null ? info.id() : "TC_GEN";
        String category = info != null ? info.category() : "General Testing";
        String screen = info != null ? info.screen() : "Home Screen";
        String description = result.getMethod().getDescription();
        long duration = result.getEndMillis() - result.getStartMillis();

        ReportManager.logPass("Test passed successfully.");
        ReportManager.addResult(tcId, result.getTestClass().getName(), description, category, screen, "PASSED", duration, null, null);
        logger.info("Test Passed: {} (Duration: {}ms)", tcId, duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        TestCaseInfo info = method.getAnnotation(TestCaseInfo.class);

        String tcId = info != null ? info.id() : "TC_GEN";
        String category = info != null ? info.category() : "General Testing";
        String screen = info != null ? info.screen() : "Home Screen";
        String description = result.getMethod().getDescription();
        long duration = result.getEndMillis() - result.getStartMillis();
        String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown Error";

        // Capture screenshot on failure
        String screenshotPath = ScreenshotUtility.captureScreenshot(tcId);
        
        ReportManager.logFail("Test failed with error: " + errorMsg, result.getThrowable());
        if (screenshotPath != null && ReportManager.getTest() != null) {
            ReportManager.getTest().addScreenCaptureFromPath(screenshotPath);
        }

        ReportManager.addResult(tcId, result.getTestClass().getName(), description, category, screen, "FAILED", duration, errorMsg, screenshotPath);
        logger.error("Test Failed: {} - Error: {}", tcId, errorMsg);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        TestCaseInfo info = method.getAnnotation(TestCaseInfo.class);

        String tcId = info != null ? info.id() : "TC_GEN";
        String category = info != null ? info.category() : "General Testing";
        String screen = info != null ? info.screen() : "Home Screen";
        String description = result.getMethod().getDescription();
        long duration = result.getEndMillis() - result.getStartMillis();

        ReportManager.addResult(tcId, result.getTestClass().getName(), description, category, screen, "SKIPPED", duration, "Test Skipped", null);
        logger.warn("Test Skipped: {}", tcId);
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Context Starting: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Context Finished: {}", context.getName());
    }
}
