package com.saveethageotag.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.saveethageotag.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReportManager {
    private static final Logger logger = LogManager.getLogger(ReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static final List<TestResult> testResults = Collections.synchronizedList(new ArrayList<>());
    private static String runTimestamp;

    // Structure to store test execution results
    public static class TestResult {
        public String tcId;
        public String suiteName;
        public String testName;
        public String category;
        public String screen;
        public String status;
        public long duration;
        public String error;
        public String screenshotPath;
        public String timestamp;

        public TestResult(String tcId, String suiteName, String testName, String category, String screen, String status, long duration, String error, String screenshotPath) {
            this.tcId = tcId;
            this.suiteName = suiteName;
            this.testName = testName;
            this.category = category;
            this.screen = screen;
            this.status = status;
            this.duration = duration;
            this.error = error;
            this.screenshotPath = screenshotPath;
            this.timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        }
    }

    public static synchronized void initReports() {
        if (extent != null) return;

        runTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String extentDir = ConfigManager.getProperty("reports.dir", "reports");
        
        // Ensure folders exist
        new File(extentDir).mkdirs();
        new File(ConfigManager.getProperty("reports.excel.dir", "excel-reports")).mkdirs();
        new File(ConfigManager.getProperty("reports.pdf.dir", "pdf-reports")).mkdirs();
        new File(ConfigManager.getProperty("reports.screenshots.dir", "screenshots")).mkdirs();
        new File(ConfigManager.getProperty("reports.logs.dir", "logs")).mkdirs();

        String extentReportPath = extentDir + "/ExtentReport_" + runTimestamp + ".html";
        logger.info("Initializing HTML Extent Report at: {}", extentReportPath);

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(extentReportPath);
        sparkReporter.config().setDocumentTitle("Saveetha GeoTag — Automation Test Report");
        sparkReporter.config().setReportName("E2E Test Execution Summary");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Platform", "Android");
        extent.setSystemInfo("Device", ConfigManager.getProperty("udid", "c91981ca"));
        extent.setSystemInfo("App Package", ConfigManager.getProperty("app.package"));
        extent.setSystemInfo("Automation Name", ConfigManager.getProperty("automation.name"));
        extent.setSystemInfo("Environment", "QA");
    }

    public static synchronized void startTest(String tcId, String testName, String category, String screen) {
        initReports();
        String fullTestTitle = tcId + " - " + testName;
        ExtentTest test = extent.createTest(fullTestTitle);
        test.assignCategory(category);
        test.assignDevice(screen);
        testThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    public static void logFail(String message, Throwable t) {
        if (getTest() != null) {
            getTest().fail(message);
            getTest().fail(t);
        }
    }

    public static void addResult(String tcId, String suiteName, String testName, String category, String screen, String status, long duration, String error, String screenshotPath) {
        TestResult result = new TestResult(tcId, suiteName, testName, category, screen, status, duration, error, screenshotPath);
        testResults.add(result);
    }

    public static List<TestResult> getTestResults() {
        return testResults;
    }

    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent HTML Report generated successfully.");
        }

        // Trigger Excel and PDF Report Generators
        try {
            String excelPath = ConfigManager.getProperty("reports.excel.dir", "excel-reports") 
                + "/SaveethaGeoTag_TestReport_" + runTimestamp + ".xlsx";
            ExcelReportGenerator.generateExcel(testResults, excelPath);
        } catch (Exception e) {
            logger.error("Failed to generate Excel report: {}", e.getMessage(), e);
        }

        try {
            String pdfPath = ConfigManager.getProperty("reports.pdf.dir", "pdf-reports") 
                + "/SaveethaGeoTag_TestReport_" + runTimestamp + ".pdf";
            PdfReportGenerator.generatePdf(testResults, pdfPath);
        } catch (Exception e) {
            logger.error("Failed to generate PDF report: {}", e.getMessage(), e);
        }
    }
}
