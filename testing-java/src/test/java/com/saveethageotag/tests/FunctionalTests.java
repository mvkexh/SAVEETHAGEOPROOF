package com.saveethageotag.tests;

import com.saveethageotag.annotations.TestCaseInfo;
import com.saveethageotag.driver.DriverManager;
import com.saveethageotag.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FunctionalTests extends BaseTest {

    @Test(description = "Verify application launches successfully without crashing")
    @TestCaseInfo(id = "TC01", category = "Functional Testing", screen = "Splash Screen")
    public void testAppLaunch() {
        LaunchPage launchPage = new LaunchPage(DriverManager.getDriver());
        Assert.assertTrue(launchPage.isAppContainerLoaded());
    }

    @Test(description = "Verify splash logo visibility during load")
    @TestCaseInfo(id = "TC02", category = "Functional Testing", screen = "Splash Screen")
    public void testSplashLogo() {
        LaunchPage launchPage = new LaunchPage(DriverManager.getDriver());
        Assert.assertTrue(launchPage.isSplashLogoVisible());
    }

    @Test(description = "Verify start screen title matches branding")
    @TestCaseInfo(id = "TC03", category = "Functional Testing", screen = "Start Screen")
    public void testStartScreenText() {
        LaunchPage launchPage = new LaunchPage(DriverManager.getDriver());
        Assert.assertEquals(launchPage.getStartScreenText(), "Saveetha GeoTag");
    }

    @Test(description = "Verify bottom navigation layout is rendered")
    @TestCaseInfo(id = "TC04", category = "Functional Testing", screen = "Home Screen")
    public void testBottomNavPresence() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        Assert.assertTrue(homePage.isBottomNavVisible());
    }

    @Test(description = "Verify navigation to Stats screen compiles")
    @TestCaseInfo(id = "TC05", category = "Functional Testing", screen = "Dashboard Screen")
    public void testNavToStats() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToStats();
        DashboardPage dashPage = new DashboardPage(DriverManager.getDriver());
        Assert.assertTrue(dashPage.isDashboardTitleVisible());
    }

    @Test(description = "Verify navigation to Verify screen compiles")
    @TestCaseInfo(id = "TC06", category = "Functional Testing", screen = "Verify Code Screen")
    public void testNavToVerify() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToVerify();
        VerifyCodePage verifyPage = new VerifyCodePage(DriverManager.getDriver());
        Assert.assertTrue(verifyPage.isInputVisible());
    }

    @Test(description = "Verify navigation to Scan screen compiles")
    @TestCaseInfo(id = "TC07", category = "Functional Testing", screen = "Scan Screen")
    public void testNavToScan() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToScan();
        ScanPage scanPage = new ScanPage(DriverManager.getDriver());
        Assert.assertTrue(scanPage.isScanHeaderVisible());
    }

    @Test(description = "Verify navigation to History screen compiles")
    @TestCaseInfo(id = "TC08", category = "Functional Testing", screen = "Captures Screen")
    public void testNavToHistory() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToHistory();
        CapturesPage capturesPage = new CapturesPage(DriverManager.getDriver());
        Assert.assertTrue(capturesPage.isCapturesTitleVisible());
    }

    @Test(description = "Verify navigation to Settings screen compiles")
    @TestCaseInfo(id = "TC09", category = "Functional Testing", screen = "Settings Screen")
    public void testNavToSettings() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToSettings();
        SettingsPage settingsPage = new SettingsPage(DriverManager.getDriver());
        Assert.assertTrue(settingsPage.isSettingsTitleVisible());
    }

    @Test(description = "Verify verification input accepts code character entries")
    @TestCaseInfo(id = "TC10", category = "Functional Testing", screen = "Verify Code Screen")
    public void testEnterVerificationCode() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToVerify();
        VerifyCodePage verifyPage = new VerifyCodePage(DriverManager.getDriver());
        verifyPage.enterCode("GP-12345");
        // Verify input does not throw and acts properly
        Assert.assertTrue(verifyPage.isInputVisible());
    }

    @Test(description = "Verify invalid code format generates warning layout")
    @TestCaseInfo(id = "TC11", category = "Functional Testing", screen = "Verify Code Screen")
    public void testInvalidCodeWarning() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToVerify();
        VerifyCodePage verifyPage = new VerifyCodePage(DriverManager.getDriver());
        verifyPage.enterCode("INVALID");
        verifyPage.clickVerify();
        Assert.assertNotNull(verifyPage.getErrorMessage());
    }

    @Test(description = "Verify scans back navigation goes back to home dashboard")
    @TestCaseInfo(id = "TC12", category = "Functional Testing", screen = "Scan Screen")
    public void testScanBackNavigation() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToScan();
        ScanPage scanPage = new ScanPage(DriverManager.getDriver());
        scanPage.clickBack();
        Assert.assertTrue(homePage.isBottomNavVisible());
    }

    @Test(description = "Verify dark mode layout switch visibility")
    @TestCaseInfo(id = "TC13", category = "Functional Testing", screen = "Settings Screen")
    public void testSettingsThemeOption() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToSettings();
        SettingsPage settingsPage = new SettingsPage(DriverManager.getDriver());
        Assert.assertTrue(settingsPage.isDarkModeSwitchVisible());
    }

    @Test(description = "Verify settings submenus navigation (About page)")
    @TestCaseInfo(id = "TC14", category = "Functional Testing", screen = "About Screen")
    public void testSettingsAboutLink() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToSettings();
        SettingsPage settingsPage = new SettingsPage(DriverManager.getDriver());
        settingsPage.navigateToAbout();
        Assert.assertTrue(homePage.isBottomNavVisible());
    }

    @Test(description = "Verify app navigates back to captures home on tab click")
    @TestCaseInfo(id = "TC15", category = "Functional Testing", screen = "Home Screen")
    public void testReturnToCaptureTab() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateToCapture();
        Assert.assertTrue(homePage.isBottomNavVisible());
    }
}
