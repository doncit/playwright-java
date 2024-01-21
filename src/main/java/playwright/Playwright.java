package playwright;

import static com.microsoft.playwright.Playwright.create;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;

public class Playwright {

  public static Page launchBrowser() {
    com.microsoft.playwright.Playwright playwright = create();
    Browser browser = playwright.chromium().launch(new LaunchOptions().setHeadless(false));

    return browser.newPage();
  }
}
