package pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.fileutils.TestData.getBuyHostingPageUrlFromTestData;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;

@Slf4j
public class BuyHostingPage {

  private final Page page;
  private static final String MODAL_SELECTOR = ".h-modal";
  private static final String MODAL_NOT_FOUND_MESSAGE = "Modal not found ";
  private static final String PAYMENT_METHODS_NOT_FOUND_MESSAGE = "Payment methods not found ";
  private static final String TWENTY_FOUR_MONTHS = "24 months";
  private static final String PAYMENT_METHODS_SELECTOR = "#payment-methods";
  private static final String CREDIT_CARD_FORM_SELECTOR = ".provider__card";
  private static final String CREDIT_CARD_FORM_NOT_FOUND_MESSAGE = "Credit card form not found ";
  private static final String TOAST_MESSAGE_SELECTOR = ".toast-message";
  private static final String ENTER_CORRECT_CARD_MESSAGE = "Please enter correct card number.";
  private static final String ERROR_MESSAGE_TOAST_NOT_FOUND_MESSAGE =
      "Error message toast not found ";
  private static final String PURCHASE_LIST_NOT_FOUND_MESSAGE = "Purchase list not found ";
  private static final String PURCHASE_LIST_SELECTOR = "div.purchase-list";
  private static final String SELECT_HOSTING_PLAN_BUTTON_SELECTOR =
      "button#hpanel_tracking-buy-hosting-select_button";
  private static final String CREDIT_CARD_INPUT_SELECTOR = "#processout-field";
  private static final String EXPIRY_DATE_SELECTOR = "[name='cc-exp']";
  private static final String CVC_CODE_SELECTOR = "[name='cc-cvc']";
  private static final String FRAME_NOT_FOUND_ERROR = "Frame with '%s' not found";
  private static final String REMOVE_WHITESPACES = "\\s";
  private static final String REMOVE_NON_DIGITS = "\\D";
  private static final String EMPTY_STRING = "";
  private final Locator twentyFourMonthsRadioButton;
  private final Locator activeBillingPeriodRadioButton;
  private final Locator chosePaymentMethodButton;
  private final Locator cardPaymentMethodLink;
  private final Locator cardholderNameInput;
  private final Locator submitPaymentButton;
  private final Locator toastMessage;

  public BuyHostingPage(Page page) {
    this.page = page;
    this.twentyFourMonthsRadioButton = page.locator("[data-qa='24-months-title']");
    this.activeBillingPeriodRadioButton = page.locator(".period.period--active");
    this.chosePaymentMethodButton = page.locator("[data-qa='purchase-button-complete']");
    this.cardPaymentMethodLink = page.locator("#processout");
    this.cardholderNameInput = page.locator("#cardholdername");
    this.submitPaymentButton = page.locator("#submit-payment");
    this.toastMessage = page.locator(TOAST_MESSAGE_SELECTOR);
  }

  public void navigate() {
    page.navigate(getBuyHostingPageUrlFromTestData());
  }

  public void submitPayment() {
    submitPaymentButton.click();
  }

  public void fillCardHolderName(String name) {
    cardholderNameInput.fill(name);
    assertThat(cardholderNameInput).hasValue(name);
  }

  public void fillCardNumber(String cardNumber) {
    Frame frame = getFrame(CREDIT_CARD_INPUT_SELECTOR);
    frame.fill(CREDIT_CARD_INPUT_SELECTOR, cardNumber);
    String inputValue = frame.inputValue(CREDIT_CARD_INPUT_SELECTOR);
    String actual = inputValue.replaceAll(REMOVE_WHITESPACES, EMPTY_STRING);
    assertThat(actual).isEqualTo(cardNumber);
  }

  public void fillExpiryDate(String expiryDate) {
    Frame frame = getFrame(EXPIRY_DATE_SELECTOR);
    frame.type(EXPIRY_DATE_SELECTOR, expiryDate);
    String inputValue = frame.inputValue(EXPIRY_DATE_SELECTOR);
    String actual = inputValue.replaceAll(REMOVE_NON_DIGITS, EMPTY_STRING);
    assertThat(actual).isEqualTo(expiryDate);
  }

  public void fillCvcCode(String cvcCode) {
    Frame frame = getFrame(CVC_CODE_SELECTOR);
    frame.fill(CVC_CODE_SELECTOR, cvcCode);
    String inputValue = frame.inputValue(CVC_CODE_SELECTOR);
    assertThat(inputValue).isEqualTo(cvcCode);
  }

  public void fillCreditCardInfo(
      String name, String cardNumber, String expiryDate, String cvcCode) {
    fillCardHolderName(name);
    fillCardNumber(cardNumber);
    fillExpiryDate(expiryDate);
    fillCvcCode(cvcCode);
  }

  public void chooseCardPaymentMethod() {
    cardPaymentMethodLink.click();
    waitForCreditCardForm();
  }

  public void choosePaymentMethod() {
    chosePaymentMethodButton.click();
    waitForPaymentMethods();
  }

  public void selectHostingPlan() {
    waitForPurchaseListToLoad();

    page.querySelectorAll(PURCHASE_LIST_SELECTOR)
        .get(0)
        .querySelectorAll(SELECT_HOSTING_PLAN_BUTTON_SELECTOR)
        .get(0)
        .click();

    waitForModal();
  }

  public void select24MonthsBillingPeriod() {
    int attempts = 3;
    while (attempts > 0) {
      try {
        twentyFourMonthsRadioButton.click();
        assertThat(activeBillingPeriodRadioButton).containsText(TWENTY_FOUR_MONTHS);
        break;
      } catch (AssertionFailedError e) {
        attempts--;
        try {
          sleep(500);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public void waitForPurchaseListToLoad() {
    try {
      page.waitForSelector(
          PURCHASE_LIST_SELECTOR, new Page.WaitForSelectorOptions().setTimeout(120000));
    } catch (PlaywrightException e) {
      throw new RuntimeException(PURCHASE_LIST_NOT_FOUND_MESSAGE + e.getMessage(), e);
    }
  }

  public void waitForInvalidCardNumberError() {
    try {
      page.waitForSelector(TOAST_MESSAGE_SELECTOR);
      assertThat(toastMessage).containsText(ENTER_CORRECT_CARD_MESSAGE);
    } catch (PlaywrightException e) {
      throw new RuntimeException(ERROR_MESSAGE_TOAST_NOT_FOUND_MESSAGE + e.getMessage(), e);
    }
  }

  private void waitForCreditCardForm() {
    try {
      page.waitForSelector(CREDIT_CARD_FORM_SELECTOR);
    } catch (PlaywrightException e) {
      throw new RuntimeException(CREDIT_CARD_FORM_NOT_FOUND_MESSAGE + e.getMessage(), e);
    }
  }

  private void waitForModal() {
    try {
      page.waitForSelector(MODAL_SELECTOR);
    } catch (PlaywrightException e) {
      throw new RuntimeException(MODAL_NOT_FOUND_MESSAGE + e.getMessage(), e);
    }
  }

  private void waitForPaymentMethods() {
    try {
      page.waitForSelector(PAYMENT_METHODS_SELECTOR);
    } catch (PlaywrightException e) {
      throw new RuntimeException(PAYMENT_METHODS_NOT_FOUND_MESSAGE + e.getMessage(), e);
    }
  }

  private Frame getFrame(String selector) {
    return page.frames().stream()
        .filter(f -> nonNull(f.querySelector(selector)))
        .findFirst()
        .orElseThrow(() -> new Error(format(FRAME_NOT_FOUND_ERROR, selector)));
  }
}
