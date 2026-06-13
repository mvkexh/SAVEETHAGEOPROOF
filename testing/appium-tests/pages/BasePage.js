/**
 * ================================================================
 * Page Object Model: BasePage
 * Base class providing common helpers for all Page Objects
 * ================================================================
 */
const { getDriver, takeScreenshot } = require('../../utils/appiumDriver');

class BasePage {
  constructor() {
    this.defaultTimeout = 15000;
  }

  get driver() {
    return getDriver();
  }

  // ── Element Finders ────────────────────────────────────────────
  async byText(text) {
    return this.driver.$(`android=new UiSelector().text("${text}")`);
  }

  async byTextContains(text) {
    return this.driver.$(`android=new UiSelector().textContains("${text}")`);
  }

  async byClass(className) {
    return this.driver.$(`android=new UiSelector().className("${className}")`);
  }

  async byId(resourceId) {
    return this.driver.$(`android=new UiSelector().resourceId("${resourceId}")`);
  }

  async byAccessibility(accessibilityId) {
    return this.driver.$(`~${accessibilityId}`);
  }

  async byDescription(desc) {
    return this.driver.$(`android=new UiSelector().description("${desc}")`);
  }

  // ── Waits ──────────────────────────────────────────────────────
  async waitForText(text, timeout = this.defaultTimeout) {
    const el = await this.byText(text);
    await el.waitForDisplayed({ timeout });
    return el;
  }

  async waitForExistence(selector, timeout = this.defaultTimeout) {
    const el = await this.driver.$(selector);
    await el.waitForExist({ timeout });
    return el;
  }

  // ── Actions ────────────────────────────────────────────────────
  async tapText(text) {
    const el = await this.waitForText(text);
    await el.click();
    await this.driver.pause(800);
  }

  async typeInField(className, value) {
    const el = await this.byClass(className);
    await el.waitForDisplayed({ timeout: this.defaultTimeout });
    await el.click();
    await el.clearValue();
    await el.setValue(value);
    return el;
  }

  async scrollToText(text) {
    await this.driver.$(`android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text("${text}"))`);
  }

  // ── Assertions ─────────────────────────────────────────────────
  async isDisplayed(element) {
    return element.isDisplayed().catch(() => false);
  }

  async isExisting(element) {
    return element.isExisting().catch(() => false);
  }

  async currentPackage() {
    return this.driver.getCurrentPackage();
  }

  async currentActivity() {
    return this.driver.getCurrentActivity();
  }

  async appState() {
    return this.driver.queryAppState('com.example.saveethageotag');
  }

  async screenshot(name) {
    return takeScreenshot(name);
  }

  async pause(ms = 1000) {
    return this.driver.pause(ms);
  }

  async back() {
    await this.driver.back();
    await this.driver.pause(800);
  }

  async windowSize() {
    return this.driver.getWindowSize();
  }
}

module.exports = BasePage;
