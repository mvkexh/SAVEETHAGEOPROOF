/**
 * Appium Configuration for Saveetha GeoTag Android App
 */
require('dotenv').config({ path: require('path').resolve(__dirname, '../.env') });

const appiumConfig = {
  // Appium Server
  hostname: process.env.APPIUM_HOST || 'localhost',
  port: parseInt(process.env.APPIUM_PORT) || 4723,
  path: '/',
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,

  // Android Capabilities
  capabilities: {
    platformName: 'Android',
    'appium:platformVersion': process.env.ANDROID_PLATFORM_VERSION || '13.0',
    'appium:deviceName': process.env.ANDROID_DEVICE_NAME || 'emulator-5554',
    'appium:automationName': 'UiAutomator2',
    'appium:appPackage': process.env.APP_PACKAGE || 'com.example.saveethageotag',
    'appium:appActivity': process.env.APP_ACTIVITY || '.MainActivity',
    'appium:app': require('path').resolve(__dirname, process.env.APP_PATH || '../app/build/outputs/apk/debug/app-debug.apk'),
    'appium:noReset': false,
    'appium:fullReset': false,
    'appium:newCommandTimeout': 300,
    'appium:androidInstallTimeout': 90000,
    'appium:adbExecTimeout': 60000,
    'appium:uiautomator2ServerInstallTimeout': 60000,
    'appium:autoGrantPermissions': true,
    'appium:skipDeviceInitialization': false,
    'appium:ignoreUnimportantViews': true,
  },

  // Timeouts
  implicitTimeout: parseInt(process.env.IMPLICIT_WAIT) || 10000,
  explicitTimeout: parseInt(process.env.EXPLICIT_WAIT) || 30000,
};

module.exports = appiumConfig;
