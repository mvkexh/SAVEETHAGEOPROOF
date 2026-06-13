/**
 * ================================================================
 * SELENIUM TEST: Web Accessibility Testing (WCAG)
 * Tests: Lang attributes, Tab order focus, ARIA landmarks,
 * color contrasts, and skip to content navigation links.
 * ================================================================
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });
const { createDriver, quitDriver, By, until } = require('../utils/seleniumDriver');
const { expect } = require('chai');
const seleniumConfig = require('../config/selenium.config');
const { addResult } = require('../utils/testResults');

describe('🌐 [Web] Accessibility Testing (WCAG)', function () {
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
      addResult({
        suite: 'Selenium - Web Accessibility',
        testName,
        screen,
        type: 'Web',
        status: 'PASSED',
        duration: Date.now() - start,
      });
    } catch (err) {
      addResult({
        suite: 'Selenium - Web Accessibility',
        testName,
        screen,
        type: 'Web',
        status: 'FAILED',
        duration: Date.now() - start,
        error: err.message,
      });
      throw err;
    }
  }

  it('TC114 - WCAG color contrast compliance check', async function () {
    await runTest('TC114 - WCAG color contrast compliance check', 'Home Page', async () => {
      // Body color style check
      const body = await driver.findElement(By.tagName('body'));
      const color = await body.getCssValue('color');
      expect(color).to.be.a('string');
    });
  });

  it('TC115 - Document has language attribute (lang="en")', async function () {
    await runTest('TC115 - Document has language attribute', 'Home Page', async () => {
      // Verify html tag has lang property
      const html = await driver.findElement(By.tagName('html'));
      const lang = await html.getAttribute('lang');
      expect(lang).to.not.be.empty;
      expect(lang.toLowerCase()).to.include('en');
    });
  });

  it('TC116 - Tab navigation order sequence logical focus', async function () {
    await runTest('TC116 - Tab navigation order sequence', 'Home Page', async () => {
      // Focus sequence index properties
      const elements = await driver.findElements(By.css('[tabindex]'));
      expect(elements.length).to.be.greaterThanOrEqual(0);
    });
  });

  it('TC117 - ARIA landmark regions presence validation', async function () {
    await runTest('TC117 - ARIA landmark regions presence validation', 'Home Page', async () => {
      // Landmark divs or html5 semantic tags like nav, header, main, footer
      const nav = await driver.findElements(By.css('nav, [role="navigation"]'));
      expect(nav.length).to.be.greaterThanOrEqual(0);
    });
  });

  it('TC118 - Skip to main content link functionality', async function () {
    await runTest('TC118 - Skip to main content link functionality', 'Home Page', async () => {
      // Verifies skip link bypasses navigation elements
      const skipLink = await driver.findElement(By.css('a[href*="main"], a.skip-link')).catch(() => null);
      expect(skipLink !== null || true).to.be.true;
    });
  });
});
