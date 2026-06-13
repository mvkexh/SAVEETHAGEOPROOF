/**
 * Page Object Model: StartPage
 * Saveetha GeoTag — Start/Splash Screen
 */
const BasePage = require('./BasePage');

class StartPage extends BasePage {
  // Selectors
  get logoImage()   { return this.driver.$('android=new UiSelector().className("android.widget.ImageView")'); }
  get packageName() { return 'com.example.saveethageotag'; }

  async waitForSplash(timeout = 5000) {
    await this.pause(timeout);
  }

  async isPackageRunning() {
    const pkg = await this.currentPackage();
    return pkg === this.packageName;
  }

  async isAppInForeground() {
    const state = await this.appState();
    return state === 4;
  }

  async isLogoVisible() {
    const { width, height } = await this.windowSize();
    return width > 0 && height > 0;
  }

  async getActivityName() {
    return this.currentActivity();
  }
}

module.exports = new StartPage();
