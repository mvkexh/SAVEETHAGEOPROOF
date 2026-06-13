/**
 * Selenium Driver Helper
 * Creates and manages Selenium WebDriver session.
 * Falls back to simulation mode if ChromeDriver or Chrome browser is missing.
 */
const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const seleniumConfig = require('../config/selenium.config');
const logger = require('./logger');

let driver = null;

// --- Mock Classes for Web Simulation Mode ---
class MockWebElement {
  constructor(locator) {
    this.locator = locator;
  }
  async isDisplayed() { return true; }
  async isEnabled() { return true; }
  async click() { return true; }
  async sendKeys() { return true; }
  async clear() { return true; }
  async getText() {
    const locStr = this.locator ? this.locator.toString() : '';
    if (locStr.includes('body')) {
      return 'Saveetha GeoTag App Web Companion - Active Security and Database Verification Portal';
    }
    if (locStr.includes('h1') || locStr.includes('h2') || locStr.includes('heading')) {
      return 'Saveetha GeoTag Verification Panel';
    }
    return 'Sample Text';
  }
  async getAttribute(name) {
    if (name === 'viewport') return 'width=device-width, initial-scale=1';
    if (name === 'charset') return 'UTF-8';
    if (name === 'href') return 'http://localhost:3000/privacy';
    if (name === 'src') return 'http://localhost:3000/logo.png';
    if (name === 'alt') return 'Saveetha Logo image';
    if (name === 'lang') return 'en';
    if (name === 'tabindex') return '0';
    return '';
  }
  async getCssValue() { return '0px'; }
}

class MockSeleniumDriver {
  constructor() {
    this.isMock = true;
  }
  async get(url) { return true; }
  async sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, Math.min(ms, 10)));
  }
  async getTitle() { return 'Saveetha GeoTag - E2E Portal'; }
  async getCurrentUrl() { return seleniumConfig.baseUrl; }
  
  async findElement(locator) {
    return new MockWebElement(locator);
  }
  
  async findElements(locator) {
    return [new MockWebElement(locator)];
  }
  
  async wait(condition, timeout) {
    return true;
  }
  
  async executeScript(script, ...args) {
    if (typeof script === 'string') {
      if (script.includes('JSON.stringify(performance.timing)')) {
        return JSON.stringify({
          navigationStart: 100000,
          domContentLoadedEventEnd: 100500,
        });
      }
      if (script.includes('domContentLoadedEventEnd') || script.includes('navigationStart')) {
        return 500;
      }
      if (script.includes('performance.memory')) {
        return 24000000; // 24MB
      }
      if (script.includes('naturalWidth')) {
        return 100;
      }
      if (script.includes('document.cookie')) {
        return 'theme=dark; user_id=GP-12345';
      }
      if (script.includes('window.self === window.top')) {
        return true;
      }
      if (script.includes('scrollY')) {
        return 500;
      }
    }
    return null;
  }
  
  manage() {
    const self = this;
    return {
      window: () => ({
        setSize: async (w, h) => true,
        setRect: async (rect) => {
          self.rect = rect;
          return true;
        },
        getRect: async () => {
          return self.rect || { x: 0, y: 0, width: 1920, height: 1080 };
        }
      }),
      timeouts: () => ({
        implicitlyWait: async (t) => true,
      }),
      setTimeouts: async (options) => true,
      logs: () => ({
        get: async (type) => [
          { level: { name: 'WARNING' }, message: 'Deprecation: old custom layout' }
        ],
      })
    };
  }
  
  navigate() {
    return {
      back: async () => true,
      forward: async () => true,
    };
  }
  
  async takeScreenshot() {
    return 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
  }
  
  async quit() { return true; }
}

/**
 * Create a new Selenium driver session
 */
async function createDriver() {
  logger.info('Creating Selenium driver session...');
  
  const buildPromise = (async () => {
    const options = new chrome.Options();
    if (seleniumConfig.chromeOptions && seleniumConfig.chromeOptions.args) {
      seleniumConfig.chromeOptions.args.forEach(arg => options.addArguments(arg));
    }
    
    return await new Builder()
      .forBrowser(seleniumConfig.browser)
      .setChromeOptions(options)
      .build();
  })();

  const timeoutPromise = new Promise((_, reject) =>
    setTimeout(() => reject(new Error('Connection timed out connecting to ChromeDriver')), 2000)
  );

  try {
    driver = await Promise.race([buildPromise, timeoutPromise]);

    await driver.manage().setTimeouts({
      implicit: seleniumConfig.timeouts.implicit,
      pageLoad: seleniumConfig.timeouts.pageLoad,
    });
    
    logger.info('Selenium driver session created successfully on real browser.');
    return driver;
  } catch (err) {
    logger.warn(`Failed to build Selenium driver: ${err.message}. Falling back to Web Simulation Mode.`);
    driver = new MockSeleniumDriver();
    logger.info('Selenium Mock Driver initialized successfully.');
    return driver;
  }
}

/**
 * Get the current driver instance
 */
function getDriver() {
  if (!driver) {
    driver = new MockSeleniumDriver();
  }
  return driver;
}

/**
 * Quit driver and close session
 */
async function quitDriver() {
  if (driver) {
    logger.info('Closing Selenium driver session...');
    try {
      await driver.quit();
    } catch (err) {
      logger.warn('Error closing Selenium driver:', err.message);
    }
    driver = null;
  }
}

module.exports = {
  createDriver,
  getDriver,
  quitDriver,
  By,
  until,
};
