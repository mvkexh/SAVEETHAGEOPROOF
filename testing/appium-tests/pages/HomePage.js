/**
 * Page Object Model: HomePage
 * Saveetha GeoTag — Home / Camera Capture Screen
 */
const BasePage = require('./BasePage');

class HomePage extends BasePage {
  // Bottom nav items
  get captureNavBtn()   { return this.byText('Capture'); }
  get statsNavBtn()     { return this.byText('Stats'); }
  get verifyNavBtn()    { return this.byText('Verify'); }
  get scanNavBtn()      { return this.byText('Scan'); }
  get historyNavBtn()   { return this.byText('History'); }
  get settingsNavBtn()  { return this.byText('Settings'); }

  // Camera controls
  get historyBtn()   { return this.byText('History'); }
  get arViewBtn()    { return this.byText('AR View'); }
  get linearLayout() { return this.byClass('android.widget.LinearLayout'); }

  async waitForHomeScreen() {
    await this.pause(4000); // allow splash + firebase auth
  }

  async isOnHomeScreen() {
    const pkg = await this.currentPackage();
    return pkg === 'com.example.saveethageotag';
  }

  async navigateTo(screenLabel) {
    await this.tapText(screenLabel);
  }

  async isNavBarVisible() {
    const el = await this.linearLayout;
    return el.isDisplayed().catch(() => false);
  }

  async goToStats()    { await this.tapText('Stats'); }
  async goToVerify()   { await this.tapText('Verify'); }
  async goToScan()     { await this.tapText('Scan'); }
  async goToHistory()  { await this.tapText('History'); }
  async goToSettings() { await this.tapText('Settings'); }
  async goToCapture()  { await this.tapText('Capture'); }
}

module.exports = new HomePage();
