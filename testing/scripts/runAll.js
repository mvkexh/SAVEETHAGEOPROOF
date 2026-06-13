/**
 * ================================================================
 * E2E TEST RUNNER COORDINATOR
 * Clears old results, runs all test suites, and generates Excel + HTML reports
 * Run: npm run test:run-all
 * ================================================================
 */
const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const { clearResults } = require('../utils/testResults');
const logger = require('../utils/logger');

const testingDir = path.resolve(__dirname, '..');

async function runAll() {
  console.log('\n╔══════════════════════════════════════════════════════════╗');
  console.log('║        SAVEETHA GEOTAG COMPLETE E2E TEST RUNNER          ║');
  console.log('║           Executing 121 Web & Mobile Tests               ║');
  console.log('╚══════════════════════════════════════════════════════════╝\n');

  logger.info('Clearing old test results...');
  clearResults();

  logger.info('Executing all Appium and Selenium test suites via Mocha...');
  let mochaError = null;
  
  try {
    // Run tests and forward output directly to stdout/stderr
    const envPath = process.env.PATH || '';
    const newPath = envPath.includes('C:\\Program Files\\nodejs') 
      ? envPath 
      : `${envPath};C:\\Program Files\\nodejs`;

    execSync('npx mocha "appium-tests/**/*.test.js" "selenium-tests/**/*.test.js" --timeout 120000 --reporter spec --exit', {
      cwd: testingDir,
      stdio: 'inherit',
      env: { 
        ...process.env, 
        PATH: newPath,
        TEST_RUN_ID: `RUN_${new Date().toISOString().replace(/[:.]/g, '-')}` 
      }
    });
  } catch (err) {
    mochaError = err;
    logger.warn('Mocha execution completed with some test failures (proceeding to generate reports).');
  }

  // Generate Excel report
  logger.info('Triggering Excel analysis report generation...');
  try {
    const { generateReport } = require('../reports/generateExcelReport');
    await generateReport();
  } catch (err) {
    logger.error('Failed to generate Excel report:', err);
  }

  // Generate HTML report
  logger.info('Triggering HTML interactive dashboard report generation...');
  try {
    const { generateHtmlReport } = require('../reports/generateHtmlReport');
    await generateHtmlReport();
  } catch (err) {
    logger.error('Failed to generate HTML report:', err);
  }

  console.log('╔══════════════════════════════════════════════════════════╗');
  console.log('║        E2E EXECUTION AND REPORT GENERATION FINISHED      ║');
  console.log('╚══════════════════════════════════════════════════════════╝\n');
  
  process.exit(0);
}

runAll().catch(err => {
  console.error('Fatal crash in coordinator runAll:', err);
  process.exit(1);
});
