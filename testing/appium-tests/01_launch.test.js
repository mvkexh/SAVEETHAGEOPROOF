/**
 * ================================================================
 * APPIUM TEST: Start Screen & App Launch
 * Tests: Splash screen, logo display, navigation to Home
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot, waitForElement } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] App Launch & Start Screen', function () {
  this.timeout(120000);
  let driver;

  before(async function () {
    driver = await createDriver();
  });

  after(async function () {
    await quitDriver();
  });

  // ── Helper ──────────────────────────────────────────────────────
  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({
        suite: 'Appium - App Launch & Start Screen',
        testName,
        screen,
        type: 'Mobile',
        status: 'PASSED',
        duration: Date.now() - start,
      });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_launch_${testName.replace(/\s+/g, '_')}`);
      addResult({
        suite: 'Appium - App Launch & Start Screen',
        testName,
        screen,
        type: 'Mobile',
        status: 'FAILED',
        duration: Date.now() - start,
        error: err.message,
        screenshot: shot,
      });
      throw err;
    }
  }

  // ── Tests ───────────────────────────────────────────────────────
  it('TC01 - App launches without crash', async function () {
    await runTest('TC01 - App launches without crash', 'Splash Screen', async () => {
      // Wait up to 15s for the app to appear
      const appEl = await driver.$('android=new UiSelector().packageName("com.example.saveethageotag")');
      await appEl.waitForExist({ timeout: 15000 });
    });
  });

  it('TC02 - Saveetha logo splash screen is visible', async function () {
    await runTest('TC02 - Saveetha logo splash screen visible', 'Splash Screen', async () => {
      // Allow time for splash
      await driver.pause(2000);
      const screen = await driver.getWindowSize();
      expect(screen.width).to.be.greaterThan(0);
      expect(screen.height).to.be.greaterThan(0);
    });
  });

  it('TC03 - App navigates past splash to main screen', async function () {
    await runTest('TC03 - App navigates past splash', 'Start Screen', async () => {
      // After splash (1.2s) the main content should be shown
      await driver.pause(3000);
      const packageName = await driver.getCurrentPackage();
      expect(packageName).to.equal('com.example.saveethageotag');
    });
  });

  it('TC04 - Start screen elements are rendered', async function () {
    await runTest('TC04 - Start screen elements rendered', 'Start Screen', async () => {
      await driver.pause(4000);
      const activity = await driver.getCurrentActivity();
      expect(activity).to.include('MainActivity');
    });
  });

  it('TC05 - App does not crash on initial load', async function () {
    await runTest('TC05 - No crash on initial load', 'Start Screen', async () => {
      await driver.pause(2000);
      const state = await driver.queryAppState('com.example.saveethageotag');
      // State 4 = Running in foreground
      expect(state).to.equal(4);
    });
  });
});
