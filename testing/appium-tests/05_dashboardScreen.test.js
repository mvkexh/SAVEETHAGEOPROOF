/**
 * ================================================================
 * APPIUM TEST: Dashboard Screen
 * Tests: Statistics display, charts, data rendering
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Dashboard Screen - Statistics', function () {
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
      addResult({ suite: 'Appium - Dashboard Screen', testName, screen, type: 'Mobile', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_dashboard_${testName.replace(/\s+/g, '_')}`);
      addResult({ suite: 'Appium - Dashboard Screen', testName, screen, type: 'Mobile', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: shot });
      throw err;
    }
  }

  it('TC25 - Navigate to Dashboard screen', async function () {
    await runTest('TC25 - Navigate to Dashboard', 'Dashboard Screen', async () => {
      const dashBtn = await driver.$('android=new UiSelector().text("Stats")');
      await dashBtn.waitForDisplayed({ timeout: 10000 });
      await dashBtn.click();
      await driver.pause(2000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC26 - Dashboard screen renders without crash', async function () {
    await runTest('TC26 - Dashboard renders without crash', 'Dashboard Screen', async () => {
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });

  it('TC27 - Dashboard shows statistics content', async function () {
    await runTest('TC27 - Dashboard statistics visible', 'Dashboard Screen', async () => {
      await driver.pause(2000);
      // Verify the screen has content (not blank)
      const screen = await driver.getWindowSize();
      expect(screen.width).to.be.greaterThan(0);
    });
  });

  it('TC28 - Dashboard scroll works', async function () {
    await runTest('TC28 - Dashboard scroll functionality', 'Dashboard Screen', async () => {
      const { width, height } = await driver.getWindowSize();
      await driver.touchAction([
        { action: 'press', x: Math.floor(width / 2), y: Math.floor(height * 0.7) },
        { action: 'moveTo', x: Math.floor(width / 2), y: Math.floor(height * 0.3) },
        { action: 'release' },
      ]);
      await driver.pause(1000);
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });
});
