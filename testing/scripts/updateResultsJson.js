const fs = require('fs');
const path = require('path');

const resultsPath = path.resolve(__dirname, '../reports/output/test-results.json');

const MAP = {
  // File 01
  "App launches without crash": "TC01 - App launches without crash",
  "Saveetha logo splash screen visible": "TC02 - Saveetha logo splash screen visible",
  "App navigates past splash": "TC03 - App navigates past splash",
  "Start screen elements rendered": "TC04 - Start screen elements rendered",
  "No crash on initial load": "TC05 - No crash on initial load",

  // File 02
  "Home screen loads": "TC06 - Home screen loads",
  "Bottom nav bar visible": "TC07 - Bottom nav bar visible",
  "Camera permission handled": "TC08 - Camera permission handled",
  "Navigate to Dashboard": "TC09 - Navigate to Dashboard",
  "Navigate to Scan screen": "TC10 - Navigate to Scan screen",
  "Navigate to History/Captures": "TC11 - Navigate to History/Captures",
  "Navigate to Settings": "TC12 - Navigate to Settings",
  "Navigate back to Home/Capture": "TC13 - Navigate back to Home/Capture",

  // File 03
  "Navigate to Verify Code": "TC14 - Navigate to Verify Code",
  "Input field visible on Verify screen": "TC15 - Input field visible on Verify screen",
  "Enter valid GP-CODE": "TC16 - Enter valid GP-CODE",
  "Verify button clickable": "TC17 - Verify button clickable",
  "Empty code shows error": "TC18 - Empty code shows error",
  "Back navigation from Verify": "TC19 - Back navigation from Verify",

  // File 04
  // TC20 is same as TC10/TC25/TC29/TC33. We distinguish by suite/screen context.
  "Scan screen loads without crash": "TC21 - Scan screen loads without crash",
  "Camera viewfinder or permission shown": "TC22 - Camera viewfinder or permission shown",
  "Scan header text visible": "TC23 - Scan header text visible",
  "Back navigation from Scan": "TC24 - Back navigation from Scan",

  // File 05
  "Dashboard renders without crash": "TC26 - Dashboard renders without crash",
  "Dashboard statistics visible": "TC27 - Dashboard statistics visible",
  "Dashboard scroll functionality": "TC28 - Dashboard scroll functionality",

  // File 06
  "Captures screen renders": "TC30 - Captures screen renders",
  "Empty state or list visible": "TC31 - Empty state or list visible",
  "Captures list scroll": "TC32 - Captures list scroll",

  // File 07
  "Settings screen renders": "TC34 - Settings screen renders",
  "Dark mode toggle present": "TC35 - Dark mode toggle present",
  "Navigate to About screen": "TC36 - Navigate to About screen",
  "Back from About to Settings": "TC37 - Back from About to Settings",
  "Navigate to Help Center": "TC38 - Navigate to Help Center",
  "Navigate to Privacy Policy": "TC39 - Navigate to Privacy Policy",
  "Navigate to Terms of Service": "TC40 - Navigate to Terms of Service",

  // File 08
  "Navigate to Home screen": "TC41 - Navigate to Home screen",
  "AR screen launches": "TC42 - AR screen launches",
  "AR screen back navigation": "TC43 - AR screen back navigation",

  // File 09
  "[E2E] App running in foreground": "TC44 - [E2E] App running in foreground",
  "[E2E] Bottom nav complete tour": "TC45 - [E2E] Bottom nav complete tour",
  "[E2E] Back button handling": "TC46 - [E2E] Back button handling",
  "[E2E] No crash after extended navigation": "TC47 - [E2E] No crash after extended navigation",
  "[E2E] Final state screenshot": "TC48 - [E2E] Final state screenshot"
};

async function updateAndRegenerate() {
  if (!fs.existsSync(resultsPath)) {
    console.error(`Results file not found at ${resultsPath}`);
    process.exit(1);
  }

  let results = JSON.parse(fs.readFileSync(resultsPath, 'utf8'));
  let updatedCount = 0;

  results.forEach(r => {
    // Check specific suite-dependent overlaps
    if (r.testName === "Navigate to Scan screen" && r.suite === "Appium - Scan Screen") {
      r.testName = "TC20 - Navigate to Scan screen";
      updatedCount++;
    } else if (r.testName === "Navigate to Dashboard" && r.suite === "Appium - Dashboard Screen") {
      r.testName = "TC25 - Navigate to Dashboard";
      updatedCount++;
    } else if (r.testName === "Navigate to Captures" && r.suite === "Appium - Captures Screen") {
      r.testName = "TC29 - Navigate to Captures";
      updatedCount++;
    } else if (r.testName === "Navigate to Settings" && r.suite === "Appium - Settings Screen") {
      r.testName = "TC33 - Navigate to Settings";
      updatedCount++;
    } else if (MAP[r.testName]) {
      r.testName = MAP[r.testName];
      updatedCount++;
    }

    // Now update category mapping
    const { TC_CATEGORIES } = require('../utils/testResults');
    const match = r.testName.match(/(TC\d+)/);
    if (match && TC_CATEGORIES[match[1]]) {
      r.category = TC_CATEGORIES[match[1]];
    }
  });

  fs.writeFileSync(resultsPath, JSON.stringify(results, null, 2), 'utf8');
  console.log(`Updated ${updatedCount} test result names in test-results.json.`);

  // Regenerate Excel Report
  console.log('Regenerating Excel report...');
  const { generateReport } = require('../reports/generateExcelReport');
  await generateReport();

  // Regenerate HTML Report
  console.log('Regenerating HTML report...');
  const { generateHtmlReport } = require('../reports/generateHtmlReport');
  await generateHtmlReport();

  console.log('All reports regenerated successfully!');
}

updateAndRegenerate().catch(err => {
  console.error('Error during regeneration:', err);
  process.exit(1);
});
