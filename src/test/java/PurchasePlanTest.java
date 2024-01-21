import static playwright.Playwright.launchBrowser;
import static utils.fileutils.Secrets.getEmailFromSecrets;
import static utils.fileutils.Secrets.getPasswordFromSecrets;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import pages.BuyHostingPage;
import pages.LoginPage;

@Slf4j
public class PurchasePlanTest {

  private static final String INVALID_CARDHOLDER_NAME = "Test";
  private static final String INVALID_CARD = "1212121212121212";
  private static final String INVALID_EXPIRY_DATE = "1126";
  private static final String INVALID_CVC = "123";

  @Test
  public void testPurchasePlanWithInvalidCard() {
    Page page = launchBrowser();

    LoginPage loginPage = new LoginPage(page);
    loginPage.login(getEmailFromSecrets(), getPasswordFromSecrets());

    BuyHostingPage buyHostingPage = new BuyHostingPage(page);
    buyHostingPage.navigate();
    buyHostingPage.selectHostingPlan();
    buyHostingPage.select24MonthsBillingPeriod();
    buyHostingPage.choosePaymentMethod();
    buyHostingPage.chooseCardPaymentMethod();
    buyHostingPage.fillCreditCardInfo(
        INVALID_CARDHOLDER_NAME, INVALID_CARD, INVALID_EXPIRY_DATE, INVALID_CVC);
    buyHostingPage.submitPayment();
    buyHostingPage.waitForInvalidCardNumberError();
  }
}
