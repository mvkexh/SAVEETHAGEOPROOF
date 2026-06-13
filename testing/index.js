/**
 * =============================================================
 * Saveetha GeoTag - Test Suite Entry Point
 * =============================================================
 * Run: node index.js           → Shows menu
 * Run: npm run test:appium     → Appium mobile tests only
 * Run: npm run test:selenium   → Selenium web tests only
 * Run: npm run test:all        → All tests + Excel report
 * =============================================================
 */

require('dotenv').config();
const { execSync } = require('child_process');
const path = require('path');
const logger = require('./utils/logger');

console.log(`
╔══════════════════════════════════════════════════════════╗
║        SAVEETHA GEOTAG - E2E TEST SUITE v1.0.0          ║
║        Mobile (Appium) + Web (Selenium) Testing         ║
╚══════════════════════════════════════════════════════════╝

Available Commands:
  npm run test:appium    → Run all Appium mobile tests
  npm run test:selenium  → Run all Selenium web tests
  npm run test:all       → Run all tests
  npm run test:report    → Generate Excel report only
  npm run test:run-all   → Run all tests + Generate Excel report

Project Structure:
  /appium-tests/         → Appium mobile tests (Android)
  /selenium-tests/       → Selenium web tests
  /reports/              → Report generator & output
  /utils/                → Shared helpers & config
  /config/               → Appium & Selenium configs
`);

logger.info('Saveetha GeoTag Test Suite initialized.');
logger.info(`Appium Host: ${process.env.APPIUM_HOST}:${process.env.APPIUM_PORT}`);
logger.info(`App Package: ${process.env.APP_PACKAGE}`);
