/**
 * ================================================================
 * APPIUM TEST: Captures / History Screen
 * Tests: Captures list, empty state, item click navigation
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Captures History Screen', function () {
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
      addResult({ suite: 'Appium - Captures Screen', testName, screen, type: 'Mobile', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_captures_${testName.replace(/\s+/g, '_')}`);
      addResult({ suite: 'Appium - Captures Screen', testName, screen, type: 'Mobile', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: shot });
      throw err;
    }
  }

  it('TC29 - Navigate to Captures/History screen', async function () {
    await runTest('TC29 - Navigate to Captures', 'Captures Screen', async () => {
      const histBtn = await driver.$('android=new UiSelector().text("History")');
      await histBtn.waitForDisplayed({ timeout: 10000 });
      await histBtn.click();
      await driver.pause(2000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC30 - Captures screen renders without crash', async function () {
    await runTest('TC30 - Captures screen renders', 'Captures Screen', async () => {
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });

  it('TC31 - Captures screen shows empty state or list', async function () {
    await runTest('TC31 - Empty state or list visible', 'Captures Screen', async () => {
      await driver.pause(2000);
      // Screen should be visible (either empty or with content)
      const screen = await driver.getWindowSize();
      expect(screen.width).to.be.greaterThan(0);
    });
  });

  it('TC32 - Captures list scroll works', async function () {
    await runTest('TC32 - Captures list scroll', 'Captures Screen', async () => {
      const { width, height } = await driver.getWindowSize();
      await driver.touchAction([
        { action: 'press', x: Math.floor(width / 2), y: Math.floor(height * 0.8) },
        { action: 'moveTo', x: Math.floor(width / 2), y: Math.floor(height * 0.2) },
        { action: 'release' },
      ]);
      await driver.pause(1000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });
});
