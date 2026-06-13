/**
 * ================================================================
 * APPIUM TEST: Full E2E Navigation Flow
 * Tests: Complete user journey through all screens
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Full E2E Navigation Flow', function () {
  this.timeout(300000);
  let driver;

  before(async function () {
    driver = await createDriver();
    await driver.pause(5000);
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({ suite: 'Appium - Full E2E Flow', testName, screen, type: 'Mobile', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_e2e_${testName.replace(/\s+/g, '_')}`);
      addResult({ suite: 'Appium - Full E2E Flow', testName, screen, type: 'Mobile', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: shot });
      throw err;
    }
  }

  it('TC44 - [E2E] App is running in foreground', async function () {
    await runTest('TC44 - [E2E] App running in foreground', 'All Screens', async () => {
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });

  it('TC45 - [E2E] Complete bottom-nav tour', async function () {
    await runTest('TC45 - [E2E] Bottom nav complete tour', 'All Bottom Nav Screens', async () => {
      const navItems = ['Stats', 'Verify', 'Scan', 'History', 'Settings', 'Capture'];

      for (const item of navItems) {
        const btn = await driver.$(`android=new UiSelector().text("${item}")`);
        const exists = await btn.isExisting().catch(() => false);
        if (exists) {
          await btn.click();
          await driver.pause(1500);
          const pkg = await driver.getCurrentPackage();
          expect(pkg).to.equal('com.example.saveethageotag');
        }
      }
    });
  });

  it('TC46 - [E2E] App handles back button correctly', async function () {
    await runTest('TC46 - [E2E] Back button handling', 'Settings Screen', async () => {
      // Go to Settings
      const settBtn = await driver.$('android=new UiSelector().text("Settings")');
      await settBtn.waitForDisplayed({ timeout: 10000 });
      await settBtn.click();
      await driver.pause(1000);

      // Press device back
      await driver.back();
      await driver.pause(1000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC47 - [E2E] App does not crash after extended use', async function () {
    await runTest('TC47 - [E2E] No crash after extended navigation', 'All Screens', async () => {
      // Navigate through several screens rapidly
      const steps = ['Stats', 'Scan', 'History', 'Capture'];
      for (const step of steps) {
        const btn = await driver.$(`android=new UiSelector().text("${step}")`);
        const exists = await btn.isExisting().catch(() => false);
        if (exists) {
          await btn.click();
          await driver.pause(800);
        }
      }
      const state = await driver.queryAppState('com.example.saveethageotag');
      expect(state).to.be.oneOf([3, 4]);
    });
  });

  it('TC48 - [E2E] Screenshot of final state', async function () {
    await runTest('TC48 - [E2E] Final state screenshot', 'Home Screen', async () => {
      const shot = await takeScreenshot('E2E_FINAL_STATE');
      expect(shot).to.be.a('string');
    });
  });
});
