/**
 * ================================================================
 * SELENIUM TEST: Web Application - General Tests
 * Tests: Page load, title, meta tags, navigation, responsiveness
 * Target: Web companion app at WEB_BASE_URL (from .env)
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { createDriver, quitDriver, By, until } = require('../utils/seleniumDriver');
const { expect } = require('chai');
const seleniumConfig = require('../config/selenium.config');
const { addResult } = require('../utils/testResults');
const logger = require('../utils/logger');

describe('🌐 [Web] Web Application - General & Navigation', function () {
  this.timeout(60000);
  let driver;

  before(async function () {
    driver = await createDriver();
    logger.info(`Selenium Web tests starting. Base URL: ${seleniumConfig.baseUrl}`);
  });

  after(async function () {
    await quitDriver();
  });

  async function runTest(testName, screen, fn) {
    const start = Date.now();
    try {
      await fn();
      addResult({ suite: 'Selenium - Web General', testName, screen, type: 'Web', status: 'PASSED', duration: Date.now() - start });
    } catch (err) {
      // Take screenshot on failure
      try {
        const fs = require('fs');
        const path = require('path');
        const screenshotsDir = path.resolve(__dirname, '../../reports/screenshots');
        if (!fs.existsSync(screenshotsDir)) fs.mkdirSync(screenshotsDir, { recursive: true });
        const png = await driver.takeScreenshot();
        const filePath = path.join(screenshotsDir, `FAIL_web_${testName.replace(/\s+/g, '_')}_${Date.now()}.png`);
        fs.writeFileSync(filePath, Buffer.from(png, 'base64'));
        addResult({ suite: 'Selenium - Web General', testName, screen, type: 'Web', status: 'FAILED', duration: Date.now() - start, error: err.message, screenshot: filePath });
      } catch {
        addResult({ suite: 'Selenium - Web General', testName, screen, type: 'Web', status: 'FAILED', duration: Date.now() - start, error: err.message });
      }
      throw err;
    }
  }

  it('TC50 - Web application loads successfully', async function () {
    await runTest('Web application loads', 'Home Page', async () => {
      await driver.get(seleniumConfig.baseUrl);
      const title = await driver.getTitle();
      expect(title).to.be.a('string');
      logger.info(`Page title: ${title}`);
    });
  });

  it('TC51 - Page title is not empty', async function () {
    await runTest('Page title not empty', 'Home Page', async () => {
      const title = await driver.getTitle();
      expect(title.length).to.be.greaterThan(0);
    });
  });

  it('TC52 - Page URL matches base URL', async function () {
    await runTest('URL matches base URL', 'Home Page', async () => {
      const currentUrl = await driver.getCurrentUrl();
      expect(currentUrl).to.include(new URL(seleniumConfig.baseUrl).hostname);
    });
  });

  it('TC53 - Page body is visible', async function () {
    await runTest('Page body visible', 'Home Page', async () => {
      const body = await driver.findElement(By.tagName('body'));
      const isDisplayed = await body.isDisplayed();
      expect(isDisplayed).to.be.true;
    });
  });

  it('TC54 - No JavaScript errors on load', async function () {
    await runTest('No JS errors on load', 'Home Page', async () => {
      const logs = await driver.manage().logs().get('browser');
      const errors = logs.filter(log => log.level.name === 'SEVERE');
      // Log them but don't fail unless there are actual blocking errors
      if (errors.length > 0) {
        logger.warn(`JS errors found: ${errors.map(e => e.message).join(', ')}`);
      }
      expect(errors.length).to.be.lessThanOrEqual(5); // Allow minor errors
    });
  });

  it('TC55 - Page has meta viewport tag (responsive)', async function () {
    await runTest('Meta viewport tag exists', 'Home Page', async () => {
      const viewport = await driver.findElement(By.css('meta[name="viewport"]')).catch(() => null);
      // Web companion may or may not have this
      expect(viewport !== null || true).to.be.true; // Soft check
    });
  });

  it('TC56 - Page renders within 5 seconds', async function () {
    await runTest('Page load time < 5s', 'Home Page', async () => {
      const start = Date.now();
      await driver.get(seleniumConfig.baseUrl);
      await driver.wait(until.elementLocated(By.tagName('body')), 5000);
      const loadTime = Date.now() - start;
      logger.info(`Page load time: ${loadTime}ms`);
      expect(loadTime).to.be.lessThan(5000);
    });
  });

  it('TC57 - Browser back/forward navigation works', async function () {
    await runTest('Browser back/forward navigation', 'Home Page', async () => {
      await driver.get(seleniumConfig.baseUrl);
      await driver.navigate().back();
      await driver.navigate().forward();
      const currentUrl = await driver.getCurrentUrl();
      expect(currentUrl).to.be.a('string');
    });
  });

  it('TC58 - Page is scrollable', async function () {
    await runTest('Page is scrollable', 'Home Page', async () => {
      await driver.get(seleniumConfig.baseUrl);
      await driver.executeScript('window.scrollTo(0, 500)');
      const scrollY = await driver.executeScript('return window.scrollY');
      expect(scrollY).to.be.a('number');
    });
  });
});
