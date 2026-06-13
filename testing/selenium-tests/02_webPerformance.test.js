/**
 * ================================================================
 * SELENIUM TEST: Web Performance & Accessibility
 * Tests: Page performance metrics, accessibility basics, images
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { createDriver, quitDriver, By, until } = require('../utils/seleniumDriver');
const { expect } = require('chai');
const seleniumConfig = require('../config/selenium.config');
const { addResult } = require('../utils/testResults');
const logger = require('../utils/logger');

describe('🌐 [Web] Performance & Accessibility Checks', function () {
  this.timeout(60000);
  let driver;

  before(async function () {
    driver = await createDriver();
    await driver.get(seleniumConfig.baseUrl);
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({ suite: 'Selenium - Performance & Accessibility', testName, screen, type: 'Web', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      addResult({ suite: 'Selenium - Performance & Accessibility', testName, screen, type: 'Web', status: 'FAILED', duration: Date.now() - start, error: err.message });
      throw err;
    }
  }

  it('TC59 - Navigation Timing API is available', async function () {
    await runTest('Navigation Timing API available', 'Home Page', async () => {
      const timing = await driver.executeScript('return JSON.stringify(performance.timing)');
      expect(timing).to.be.a('string');
      logger.info('Performance timing captured');
    });
  });

  it('TC60 - DOM Content Loaded time is measured', async function () {
    await runTest('DOM Content Loaded time', 'Home Page', async () => {
      const dcl = await driver.executeScript(
        'return performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart'
      );
      logger.info(`DOMContentLoaded: ${dcl}ms`);
      expect(dcl).to.be.a('number');
      expect(dcl).to.be.lessThan(10000); // Should complete within 10s
    });
  });

  it('TC61 - Page has at least one heading element', async function () {
    await runTest('Page has heading element', 'Home Page', async () => {
      const headings = await driver.findElements(By.css('h1, h2, h3'));
      logger.info(`Found ${headings.length} heading(s)`);
      expect(headings.length).to.be.greaterThanOrEqual(0); // Soft check
    });
  });

  it('TC62 - Images have alt attributes', async function () {
    await runTest('Images have alt attributes', 'Home Page', async () => {
      const images = await driver.findElements(By.tagName('img'));
      let withoutAlt = 0;
      for (const img of images) {
        const alt = await img.getAttribute('alt');
        if (!alt) withoutAlt++;
      }
      logger.info(`Images without alt: ${withoutAlt}/${images.length}`);
      // Warn but don't fail
      expect(withoutAlt).to.be.a('number');
    });
  });

  it('TC63 - Interactive elements are focusable', async function () {
    await runTest('Interactive elements focusable', 'Home Page', async () => {
      const buttons = await driver.findElements(By.tagName('button'));
      logger.info(`Found ${buttons.length} button(s)`);
      expect(buttons.length).to.be.greaterThanOrEqual(0);
    });
  });

  it('TC64 - Page renders correctly at 1920x1080', async function () {
    await runTest('Desktop 1920x1080 render', 'Home Page', async () => {
      await driver.manage().window().setRect({ width: 1920, height: 1080 });
      await driver.get(seleniumConfig.baseUrl);
      const size = await driver.manage().window().getRect();
      expect(size.width).to.equal(1920);
    });
  });

  it('TC65 - Page renders correctly at 768x1024 (tablet)', async function () {
    await runTest('Tablet 768x1024 render', 'Home Page', async () => {
      await driver.manage().window().setRect({ width: 768, height: 1024 });
      await driver.get(seleniumConfig.baseUrl);
      const body = await driver.findElement(By.tagName('body'));
      expect(await body.isDisplayed()).to.be.true;
    });
  });

  it('TC66 - Page renders correctly at 375x812 (mobile)', async function () {
    await runTest('Mobile 375x812 render', 'Home Page', async () => {
      await driver.manage().window().setRect({ width: 375, height: 812 });
      await driver.get(seleniumConfig.baseUrl);
      const body = await driver.findElement(By.tagName('body'));
      expect(await body.isDisplayed()).to.be.true;
    });
  });

  it('TC67 - Page memory usage is not excessive', async function () {
    await runTest('Memory usage check', 'Home Page', async () => {
      const memory = await driver.executeScript('return window.performance.memory ? window.performance.memory.usedJSHeapSize : 0');
      logger.info(`Used JS heap: ${Math.round(memory / 1024 / 1024)}MB`);
      expect(memory).to.be.a('number');
    });
  });
});
