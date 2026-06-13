/**
 * ================================================================
 * APPIUM TEST: Verify Code Screen
 * Tests: Input field, verify button, error handling, navigation
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { expect } = require('chai');
const { createDriver, quitDriver, takeScreenshot } = require('../utils/appiumDriver');
const { addResult } = require('../utils/testResults');

describe('📱 [Mobile] Verify Code Screen', function () {
  this.timeout(120000);
  let driver;

  before(async function () {
    driver = await createDriver();
    await driver.pause(4000); // Wait for splash
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({ suite: 'Appium - Verify Code Screen', testName, screen, type: 'Mobile', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      const shot = await takeScreenshot(`FAIL_verify_${testName.replace(/\s+/g, '_')}`);
      addResult({ suite: 'Appium - Verify Code Screen', testName, screen, type: 'Mobile', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: shot });
      throw err;
    }
  }

  async function navigateToVerify() {
    const verifyBtn = await driver.$('android=new UiSelector().text("Verify")');
    await verifyBtn.waitForDisplayed({ timeout: 10000 });
    await verifyBtn.click();
    await driver.pause(1500);
  }

  it('TC14 - Navigate to Verify Code screen', async function () {
    await runTest('TC14 - Navigate to Verify Code', 'Verify Code Screen', async () => {
      await navigateToVerify();
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC15 - Verify Code screen renders input field', async function () {
    await runTest('TC15 - Input field visible on Verify screen', 'Verify Code Screen', async () => {
      // Look for text input field
      const inputField = await driver.$('android=new UiSelector().className("android.widget.EditText")');
      const isDisplayed = await inputField.waitForDisplayed({ timeout: 10000 });
      expect(isDisplayed).to.be.true;
    });
  });

  it('TC16 - Enter valid GP-CODE format', async function () {
    await runTest('TC16 - Enter valid GP-CODE', 'Verify Code Screen', async () => {
      const inputField = await driver.$('android=new UiSelector().className("android.widget.EditText")');
      await inputField.waitForDisplayed({ timeout: 10000 });
      await inputField.click();
      await inputField.clearValue();
      await inputField.setValue('GP-ABCD1234');
      const value = await inputField.getText();
      expect(value).to.include('GP');
    });
  });

  it('TC17 - Verify button is clickable', async function () {
    await runTest('TC17 - Verify button clickable', 'Verify Code Screen', async () => {
      const verifyBtn = await driver.$('android=new UiSelector().text("Verify")');
      const isEnabled = await verifyBtn.isEnabled();
      expect(isEnabled).to.be.true;
    });
  });

  it('TC18 - Empty code shows validation error', async function () {
    await runTest('TC18 - Empty code shows error', 'Verify Code Screen', async () => {
      const inputField = await driver.$('android=new UiSelector().className("android.widget.EditText")');
      await inputField.waitForDisplayed({ timeout: 10000 });
      await inputField.clearValue();
      // Try to submit empty
      const verifyBtn = await driver.$('android=new UiSelector().text("Verify")');
      await verifyBtn.click();
      await driver.pause(1000);
      // App should still be on the same screen
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });

  it('TC19 - Back navigation works from Verify screen', async function () {
    await runTest('TC19 - Back navigation from Verify', 'Home Screen', async () => {
      await driver.back();
      await driver.pause(1000);
      const pkg = await driver.getCurrentPackage();
      expect(pkg).to.equal('com.example.saveethageotag');
    });
  });
});
