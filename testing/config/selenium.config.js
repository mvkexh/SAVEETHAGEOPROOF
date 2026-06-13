/**
 * Selenium WebDriver Configuration
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });

const seleniumConfig = {
  baseUrl: process.env.WEB_BASE_URL || 'http://localhost:3000',
  browser: process.env.BROWSER || 'chrome',
  headless: process.env.HEADLESS === 'true',

  chromeOptions: {
    args: [
      '--no-sandbox',
      '--disable-dev-shm-usage',
      '--disable-gpu',
      '--window-size=1920,1080',
      ...(process.env.HEADLESS === 'true' ? ['--headless=new'] : []),
    ],
  },

  timeouts: {
    implicit: parseInt(process.env.IMPLICIT_WAIT) || 10000,
    pageLoad: 30000,
    script: 30000,
  },

  reportDir: process.env.REPORT_OUTPUT_DIR || './reports/output',
};

module.exports = seleniumConfig;
