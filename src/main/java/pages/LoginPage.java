package pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.util.Objects.nonNull;
import static utils.fileutils.TestData.getLoginUrlFromTestData;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginPage {

  private final Page page;
  private final Locator emailInput;
  private final Locator passwordInput;
  private final Locator loginButton;
  private static final String HOME_PAGE_SELECTOR = ".h-page.home-page";
  private static final String LOGIN_SUCCESSFUL_MESSAGE = "Login successful!";
  private static final String LOGIN_FAILED_MESSAGE =
      "Login failed: An error occurred during waiting for the Home page selector";

  public LoginPage(Page page) {
    this.page = page;
    this.emailInput = page.locator("#email-input");
    this.passwordInput = page.locator("#password-input");
    this.loginButton = page.locator("[data-qa='login-button']");
  }

  public void navigate() {
    page.navigate(getLoginUrlFromTestData());
  }

  public void fillEmail(String email) {
    emailInput.fill(email);
    assertThat(emailInput).hasValue(email);
  }

  public void fillPassword(String password) {
    passwordInput.fill(password);
    assertThat(passwordInput).hasValue(password);
  }

  public void login(String email, String password) {
    navigate();
    fillEmail(email);
    fillPassword(password);
    loginButton.click();

    waitForLoginResult();
  }

  private void waitForLoginResult() {
    try {
      ElementHandle successElement = page.waitForSelector(HOME_PAGE_SELECTOR);

      if (nonNull(successElement)) {
        log.info(LOGIN_SUCCESSFUL_MESSAGE);
      }

    } catch (PlaywrightException e) {
      throw new RuntimeException(LOGIN_FAILED_MESSAGE + e.getMessage(), e);
    }
  }
}
