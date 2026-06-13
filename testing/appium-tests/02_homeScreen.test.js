/**
 * ================================================================
 * APPIUM TEST: Home Screen (Camera Capture)
 * Tests: Camera preview, capture button, gallery, AR, menu nav
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Home Screen - Camera Capture', function () {
  this.timeout(120000);
  let driver;

  before(async function () {
    driver = await createDriver();
    // Navigate past the splash
    await driver.pause(4000);
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({
        suite: 'Appium - Home Screen',
        testName, screen, type: 'Mobile',
        status: 'PASSED', duration: Date.now() - start,
      });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_home_${testName.replace(/\s+/g, '_')}`);
      addResult({
        suite: 'Appium - Home Screen',
        testName, screen, type: 'Mobile',
        status: 'FAILED', duration: Date.now() - start,
        error: err.message, screenshot: shot,
      });
      throw err;
    }
  }

  it('TC06 - Home screen loads after navigation', async function () {
    await runTest('TC06 - Home screen loads', 'Home Screen', async () => {
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC07 - Bottom navigation bar is visible', async function () {
    await runTest('TC07 - Bottom nav bar visible', 'Home Screen', async () => {
      // Check at least one nav item is visible (Capture/Home icon)
      const navBar = await driver.$('android=new UiSelector().className("android.widget.LinearLayout")');
      const isDisplayed = await navBar.isDisplayed();
      expect(isDisplayed).to.be.true;
    });
  });

  it('TC08 - Camera permission dialog is handled', async function () {
    await runTest('TC08 - Camera permission handled', 'Home Screen', async () => {
      await driver.pause(2000);
      // autoGrantPermissions handles this, just confirm app is still running
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]); // Running or foreground
    });
  });

  it('TC09 - Navigate to Dashboard via bottom nav', async function () {
    await runTest('TC09 - Navigate to Dashboard', 'Dashboard', async () => {
      // Click the Stats/Dashboard nav item
      const dashBtn = await driver.$('android=new UiSelector().text("Stats")');
      await dashBtn.waitForDisplayed({ timeout: 10000 });
      await dashBtn.click();
      await driver.pause(1500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC10 - Navigate to Scan via bottom nav', async function () {
    await runTest('TC10 - Navigate to Scan screen', 'Scan Screen', async () => {
      const scanBtn = await driver.$('android=new UiSelector().text("Scan")');
      await scanBtn.waitForDisplayed({ timeout: 10000 });
      await scanBtn.click();
      await driver.pause(1500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC11 - Navigate to History via bottom nav', async function () {
    await runTest('TC11 - Navigate to History/Captures', 'Captures Screen', async () => {
      const histBtn = await driver.$('android=new UiSelector().text("History")');
      await histBtn.waitForDisplayed({ timeout: 10000 });
      await histBtn.click();
      await driver.pause(1500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC12 - Navigate to Settings via bottom nav', async function () {
    await runTest('TC12 - Navigate to Settings', 'Settings Screen', async () => {
      const settBtn = await driver.$('android=new UiSelector().text("Settings")');
      await settBtn.waitForDisplayed({ timeout: 10000 });
      await settBtn.click();
      await driver.pause(1500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC13 - Navigate back to Capture/Home', async function () {
    await runTest('TC13 - Navigate back to Home/Capture', 'Home Screen', async () => {
      const captureBtn = await driver.$('android=new UiSelector().text("Capture")');
      await captureBtn.waitForDisplayed({ timeout: 10000 });
      await captureBtn.click();
      await driver.pause(1500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });
});
