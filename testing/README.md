# Saveetha GeoTag — E2E Test Suite

## 📁 Project Structure

```
testing/
├── appium-tests/                  ← Android Mobile Tests (Appium)
│   ├── 01_launch.test.js          → App launch & splash screen (TC01-TC05)
│   ├── 02_homeScreen.test.js      → Home + bottom navigation (TC06-TC13)
│   ├── 03_verifyCode.test.js      → Verify Code screen (TC14-TC19)
│   ├── 04_scanScreen.test.js      → QR Code scan screen (TC20-TC24)
│   ├── 05_dashboardScreen.test.js → Dashboard/Stats screen (TC25-TC28)
│   ├── 06_capturesScreen.test.js  → Captures/History screen (TC29-TC32)
│   ├── 07_settingsScreen.test.js  → Settings + sub-screens (TC33-TC40)
│   ├── 08_arScreen.test.js        → AR Screen (TC41-TC43)
│   └── 09_e2eFlow.test.js         → Full E2E journey (TC44-TC48)
│
├── selenium-tests/                ← Web Tests (Selenium WebDriver)
│   ├── 01_webGeneral.test.js      → Load, title, nav, scroll (TC50-TC58)
│   └── 02_webPerformance.test.js  → Performance & a11y (TC59-TC67)
│
├── reports/
│   ├── generateExcelReport.js     ← Excel report generator
│   ├── output/                    → Generated .xlsx reports saved here
│   ├── screenshots/               → Auto-captured failure screenshots
│   └── logs/                      → test-run.log, errors.log
│
├── config/
│   ├── appium.config.js           → Appium capabilities & timeouts
│   └── selenium.config.js         → Selenium browser & URL config
│
├── utils/
│   ├── appiumDriver.js            → Driver factory + tap/scroll helpers
│   ├── testResults.js             → Result accumulator (JSON → Excel)
│   └── logger.js                  → Winston logger
│
├── .env                           → Environment configuration
├── package.json
└── index.js                       → Entry point / menu
```

---

## ⚡ Quick Start

### 1. Install Dependencies
```bash
cd testing
npm install
```

### 2. Configure Environment
Edit `.env` with your device/emulator settings:
```env
ANDROID_DEVICE_NAME=emulator-5554
ANDROID_PLATFORM_VERSION=13.0
APP_PATH=../app/build/outputs/apk/debug/app-debug.apk
WEB_BASE_URL=http://localhost:3000
```

### 3. Start Appium Server
```bash
# Install Appium globally if not installed
npm install -g appium
appium driver install uiautomator2

# Start server
appium --port 4723
```

### 4. Run Tests

| Command | Description |
|---------|-------------|
| `npm run test:appium` | Run all Appium mobile tests |
| `npm run test:selenium` | Run all Selenium web tests |
| `npm run test:all` | Run everything |
| `npm run test:report` | Generate Excel report only |
| `npm run test:run-all` | Run all tests + generate report |

---

## 📊 Excel Report — 5 Sheets

| Sheet | Content |
|-------|---------|
| 📊 Executive Summary | KPI cards, pass rate, suite breakdown |
| 📋 All Test Results | Full table with status coloring |
| ❌ Failed Tests | Detailed failure analysis with errors |
| 🗺 Screen Coverage | All 17 screens coverage matrix |
| ⏱ Test Timeline | Chronological execution timeline |

---

## 📱 Test Coverage — 48 Mobile + 18 Web = **66 Total Test Cases**

| Screen | Tests |
|--------|-------|
| App Launch / Splash | TC01–TC05 |
| Home Screen + Bottom Nav | TC06–TC13 |
| Verify Code Screen | TC14–TC19 |
| Scan Screen (QR) | TC20–TC24 |
| Dashboard Screen | TC25–TC28 |
| Captures / History | TC29–TC32 |
| Settings + Sub-pages | TC33–TC40 |
| AR Screen | TC41–TC43 |
| Full E2E Journey | TC44–TC48 |
| Web: General | TC50–TC58 |
| Web: Performance/A11y | TC59–TC67 |

---

## 🔧 Prerequisites

- **Node.js** ≥ 18.0.0
- **Java JDK** ≥ 11 (for Appium)
- **Android SDK** / Android Studio installed
- **Appium** v2 installed globally: `npm install -g appium`
- **UiAutomator2 driver**: `appium driver install uiautomator2`
- **Chrome** browser + matching `chromedriver` (for Selenium)
- Android emulator running OR physical device connected via USB debugging

---

## 📸 Screenshots
Failure screenshots are auto-saved to `reports/screenshots/` during test runs.
