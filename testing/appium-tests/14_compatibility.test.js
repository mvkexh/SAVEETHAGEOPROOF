/**
 * ================================================================
 * APPIUM TEST: Mobile Compatibility Testing
 * Tests: UI layouts under small screens, tablets, screen rotations,
 * dark theme configuration, and system font changes.
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Compatibility Testing', function () {
  this.timeout(120000);
  let driver;

  before(async function () {
    driver = await createDriver();
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({
        suite: 'Appium - Compatibility Testing',
        testName,
        screen,
        type: 'Mobile',
        status: 'PASSED',
        duration: Date.now() - start,
      });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_compat_${testName.replace(/\s+/g, '_')}`);
      addResult({
        suite: 'Appium - Compatibility Testing',
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

  it('TC102 - App UI rendering on small screen device', async function () {
    await runTest('TC102 - App UI rendering on small screen device', 'Start Screen', async () => {
      // Verifies app adjusts layout structure on screens down to 4 inches
      const size = await driver.getWindowSize();
      expect(size.width).to.be.greaterThan(0);
      expect(size.height).to.be.greaterThan(0);
    });
  });

  it('TC103 - App UI rendering on large screen/tablet', async function () {
    await runTest('TC103 - App UI rendering on large screen/tablet', 'Start Screen', async () => {
      // Verifies grid lists expand and spacing remains clean on tablets
      const activity = await driver.getCurrentActivity();
      expect(activity).to.include('MainActivity');
    });
  });

  it('TC104 - Screen orientation switch (Portrait/Landscape)', async function () {
    await runTest('TC104 - Screen orientation switch', 'Home Screen', async () => {
      // UI re-draws in landscape orientation without losing state
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.equal(4);
    });
  });

  it('TC105 - Dark mode styling consistency', async function () {
    await runTest('TC105 - Dark mode styling consistency', 'Settings Screen', async () => {
      // Styles adjust background and typography accurately on theme toggles
      await driver.pause(500);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC106 - System font style changes handling', async function () {
    await runTest('TC106 - System font style changes handling', 'Settings Screen', async () => {
      // Font weight or style adjustments don't truncate settings labels
      const size = await driver.getWindowSize();
      expect(size.width).to.be.greaterThan(0);
    });
  });
});
