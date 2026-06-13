/**
 * Test Results Collector
 * Accumulates test results from all suites for Excel reporting
 */
const fs = require('fs');
const path = require('path');

const RESULTS_FILE = path.resolve(__dirname, '../reports/output/test-results.json');

// --- 11 Standard Testing Categories Mapping ---
const TC_CATEGORIES = {
  // Mobile Launch
  'TC01': 'Functional Testing',
  'TC02': 'UI/UX Testing',
  'TC03': 'Mobile-Specific Testing',
  'TC04': 'UI/UX Testing',
  'TC05': 'Regression Testing',
  
  // Mobile HomeScreen
  'TC06': 'Functional Testing',
  'TC07': 'UI/UX Testing',
  'TC08': 'Security Testing',
  'TC09': 'Functional Testing',
  'TC10': 'Functional Testing',
  'TC11': 'Functional Testing',
  'TC12': 'Functional Testing',
  'TC13': 'Regression Testing',

  // Mobile Verify Code
  'TC14': 'Functional Testing',
  'TC15': 'UI/UX Testing',
  'TC16': 'Functional Testing',
  'TC17': 'UI/UX Testing',
  'TC18': 'Functional Testing',
  'TC19': 'Regression Testing',

  // Mobile Scan Screen
  'TC20': 'Functional Testing',
  'TC21': 'Regression Testing',
  'TC22': 'Mobile-Specific Testing',
  'TC23': 'UI/UX Testing',
  'TC24': 'Regression Testing',

  // Mobile Dashboard
  'TC25': 'Functional Testing',
  'TC26': 'Regression Testing',
  'TC27': 'Functional Testing',
  'TC28': 'UI/UX Testing',

  // Mobile Captures
  'TC29': 'Functional Testing',
  'TC30': 'Regression Testing',
  'TC31': 'Functional Testing',
  'TC32': 'UI/UX Testing',

  // Mobile Settings
  'TC33': 'Functional Testing',
  'TC34': 'UI/UX Testing',
  'TC35': 'UI/UX Testing',
  'TC36': 'Functional Testing',
  'TC37': 'Regression Testing',
  'TC38': 'Functional Testing',
  'TC39': 'Functional Testing',
  'TC40': 'Functional Testing',

  // Mobile AR
  'TC41': 'Regression Testing',
  'TC42': 'Mobile-Specific Testing',
  'TC43': 'Regression Testing',

  // Mobile E2E
  'TC44': 'End-to-End (E2E) Testing',
  'TC45': 'End-to-End (E2E) Testing',
  'TC46': 'End-to-End (E2E) Testing',
  'TC47': 'Regression Testing',
  'TC48': 'UI/UX Testing',

  // Mobile Security (New)
  'TC49': 'Security Testing',
  'TC80': 'Security Testing',
  'TC81': 'Security Testing',
  'TC82': 'Security Testing',
  'TC83': 'Security Testing',
  'TC84': 'Security Testing',

  // Mobile API (New)
  'TC85': 'API Testing',
  'TC86': 'API Testing',
  'TC87': 'API Testing',
  'TC88': 'API Testing',
  'TC89': 'API Testing',
  'TC90': 'API Testing',

  // Mobile Database (New)
  'TC91': 'Database Testing',
  'TC92': 'Database Testing',
  'TC93': 'Database Testing',
  'TC94': 'Database Testing',
  'TC95': 'Database Testing',
  'TC96': 'Database Testing',

  // Mobile Accessibility (New)
  'TC97': 'Accessibility Testing',
  'TC98': 'Accessibility Testing',
  'TC99': 'Accessibility Testing',
  'TC100': 'Accessibility Testing',
  'TC101': 'Accessibility Testing',

  // Mobile Compatibility (New)
  'TC102': 'Compatibility Testing',
  'TC103': 'Compatibility Testing',
  'TC104': 'Compatibility Testing',
  'TC105': 'Compatibility Testing',
  'TC106': 'Compatibility Testing',

  // Mobile Performance (New)
  'TC107': 'Performance Testing',
  'TC108': 'Performance Testing',
  'TC109': 'Performance Testing',
  'TC110': 'Performance Testing',
  'TC111': 'Performance Testing',

  // Web General
  'TC50': 'Functional Testing',
  'TC51': 'Functional Testing',
  'TC52': 'Functional Testing',
  'TC53': 'Functional Testing',
  'TC54': 'API Testing',
  'TC55': 'Compatibility Testing',
  'TC56': 'Performance Testing',
  'TC57': 'Regression Testing',
  'TC58': 'UI/UX Testing',

  // Web Performance
  'TC59': 'Performance Testing',
  'TC60': 'Performance Testing',
  'TC61': 'Accessibility Testing',
  'TC62': 'Accessibility Testing',
  'TC63': 'Accessibility Testing',
  'TC64': 'UI/UX Testing',
  'TC65': 'UI/UX Testing',
  'TC66': 'UI/UX Testing',
  'TC67': 'Performance Testing',

  // Web UI
  'TC68': 'Functional Testing',
  'TC69': 'Functional Testing',
  'TC70': 'Functional Testing',
  'TC71': 'Accessibility Testing',
  'TC72': 'Accessibility Testing',
  'TC73': 'Accessibility Testing',
  'TC74': 'Functional Testing',
  'TC75': 'Accessibility Testing',

  // Web Security (New)
  'TC76': 'Security Testing',
  'TC77': 'Security Testing',
  'TC78': 'Security Testing',
  'TC79': 'Security Testing',
  'TC112': 'Security Testing',
  'TC113': 'Security Testing',

  // Web Accessibility (New)
  'TC114': 'Accessibility Testing',
  'TC115': 'Accessibility Testing',
  'TC116': 'Accessibility Testing',
  'TC117': 'Accessibility Testing',
  'TC118': 'Accessibility Testing',

  // Web Regression (New)
  'TC119': 'Compatibility Testing',
  'TC120': 'UI/UX Testing',
  'TC121': 'Regression Testing',
};

/**
 * Helper to get category name from TC identifier inside testName
 */
function getCategoryForTest(testName, defaultCategory = 'Functional Testing') {
  if (!testName) return defaultCategory;
  const match = testName.match(/(TC\d+)/);
  if (match && TC_CATEGORIES[match[1]]) {
    return TC_CATEGORIES[match[1]];
  }
  return defaultCategory;
}

/**
 * Ensure output directory exists
 */
function ensureOutputDir() {
  const dir = path.dirname(RESULTS_FILE);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

/**
 * Load existing results or return empty array
 */
function loadResults() {
  ensureOutputDir();
  if (fs.existsSync(RESULTS_FILE)) {
    try {
      return JSON.parse(fs.readFileSync(RESULTS_FILE, 'utf8'));
    } catch {
      return [];
    }
  }
  return [];
}

/**
 * Save results to JSON file
 */
function saveResults(results) {
  ensureOutputDir();
  fs.writeFileSync(RESULTS_FILE, JSON.stringify(results, null, 2));
}

/**
 * Add a single test result
 */
function addResult(result) {
  const results = loadResults();
  const matchedCategory = getCategoryForTest(result.testName, result.category);
  
  results.push({
    ...result,
    category: matchedCategory,
    timestamp: new Date().toISOString(),
    runId: process.env.TEST_RUN_ID || new Date().toISOString().replace(/[:.]/g, '-'),
  });
  saveResults(results);
}

/**
 * Clear all results
 */
function clearResults() {
  ensureOutputDir();
  saveResults([]);
}

/**
 * Get summary statistics
 */
function getSummary() {
  const results = loadResults();
  return {
    total: results.length,
    passed: results.filter(r => r.status === 'PASSED').length,
    failed: results.filter(r => r.status === 'FAILED').length,
    skipped: results.filter(r => r.status === 'SKIPPED').length,
    duration: results.reduce((sum, r) => sum + (r.duration || 0), 0),
    byType: {
      mobile: results.filter(r => r.type === 'Mobile'),
      web: results.filter(r => r.type === 'Web'),
    },
  };
}

module.exports = { addResult, clearResults, loadResults, getSummary, TC_CATEGORIES };
