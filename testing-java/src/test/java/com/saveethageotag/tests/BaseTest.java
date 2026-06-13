package com.saveethageotag.tests;

import com.saveethageotag.driver.DriverManager;
import com.saveethageotag.utils.ReportManager;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Listeners(com.saveethageotag.listeners.TestListener.class)
public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite
    public void setupSuite() {
        logger.info("================ STARTING TEST SUITE ================");
        ReportManager.initReports();
    }

    @AfterSuite
    public void tearDownSuite() {
        logger.info("================ FINISHING TEST SUITE ================");
        DriverManager.quitDriver();
        ReportManager.flushReports();
    }
}
