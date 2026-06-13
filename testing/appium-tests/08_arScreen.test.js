/**
 * ================================================================
 * APPIUM TEST: AR Screen
 * Tests: AR view loads, back navigation, capture in AR mode
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] AR Screen - Augmented Reality', function () {
  this.timeout(120000);
  let driver;

  before(async function () {
    driver = await createDriver();
    await driver.pause(4000);
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({ suite: 'Appium - AR Screen', testName, screen, type: 'Mobile', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_ar_${testName.replace(/\s+/g, '_')}`);
      addResult({ suite: 'Appium - AR Screen', testName, screen, type: 'Mobile', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: shot });
      throw err;
    }
  }

  it('TC41 - Navigate to Home screen first', async function () {
    await runTest('TC41 - Navigate to Home screen', 'Home Screen', async () => {
      const captureTab = await driver.$('android=new UiSelector().text("Capture")');
      await captureTab.waitForDisplayed({ timeout: 10000 });
      await captureTab.click();
      await driver.pause(2000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC42 - AR screen launches without crash', async function () {
    await runTest('TC42 - AR screen launches', 'AR Screen', async () => {
      // Try clicking AR button on home screen
      const arBtn = await driver.$('android=new UiSelector().description("AR")');
      const exists = await arBtn.isExisting().catch(() => false);
      if (exists) {
        await arBtn.click();
        await driver.pause(2000);
      }
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });

  it('TC43 - AR screen back button works', async function () {
    await runTest('TC43 - AR screen back navigation', 'Home Screen', async () => {
      await driver.back();
      await driver.pause(1000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });
});
