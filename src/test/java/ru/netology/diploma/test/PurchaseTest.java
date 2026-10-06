package ru.netology.diploma.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.diploma.data.CardData;
import ru.netology.diploma.data.DataHelper;
import ru.netology.diploma.db.DbHelper;
import ru.netology.diploma.page.PaymentPage;
import ru.netology.diploma.page.TourPage;

import java.sql.SQLException;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PurchaseTest {
    private DbHelper dbHelper;
    private TourPage tourPage;

    @BeforeAll
    static void configureSelenide() {
        Configuration.browser = "chrome";
        Configuration.remote = System.getProperty("selenide.remote", "http://localhost:4444/wd/hub");
        Configuration.timeout = 10_000;
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "true"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--no-sandbox"
        );
        Configuration.browserCapabilities = chromeOptions;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @BeforeEach
    void setUp() {
        dbHelper = new DbHelper();
        tourPage = new TourPage();
        tourPage.open();
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }

    @Test
    void debitCardApprovedPaymentIsSavedToDatabase() throws SQLException, InterruptedException, IOException {
        CardData card = DataHelper.approvedCard();
        Set<String> knownIds = dbHelper.paymentIds();

        PaymentPage paymentPage = tourPage.choosePaymentMethod("Купить");
        paymentPage.fillCard(card).submit();
        String actualNotification = paymentPage.captureResult("approved-debit-payment");

        DbHelper.PaymentRecord payment = dbHelper.waitForNewPayment(knownIds);
        assertNotNull(payment, "В payment_entity не появилась новая запись");
        assertEquals("APPROVED", payment.getStatus());
        assertEquals(4_500_000, payment.getAmount());
        assertFalse(dbHelper.hasCardDataColumns("payment_entity"), "В таблице платежей есть столбец с данными карты");
        paymentPage.shouldShowResult(actualNotification, "Операция одобрена Банком.");
    }

    @Test
    void debitCardDeclinedPaymentIsSavedToDatabase() throws SQLException, InterruptedException, IOException {
        CardData card = DataHelper.declinedCard();
        Set<String> knownIds = dbHelper.paymentIds();

        PaymentPage paymentPage = tourPage.choosePaymentMethod("Купить");
        paymentPage.fillCard(card).submit();
        String actualNotification = paymentPage.captureResult("declined-debit-payment");

        DbHelper.PaymentRecord payment = dbHelper.waitForNewPayment(knownIds);
        assertNotNull(payment, "Отказанный платёж отсутствует в payment_entity");
        assertEquals("DECLINED", payment.getStatus());
        assertEquals(4_500_000, payment.getAmount());
        assertFalse(dbHelper.hasCardDataColumns("payment_entity"), "В таблице платежей есть столбец с данными карты");
        paymentPage.shouldShowResult(actualNotification, "Ошибка! Банк отказал в проведении операции.");
    }

    @Test
    void creditApprovedRequestIsSavedToDatabase() throws SQLException, InterruptedException, IOException {
        CardData card = DataHelper.approvedCard();
        Set<String> knownIds = dbHelper.creditIds();

        PaymentPage paymentPage = tourPage.choosePaymentMethod("Купить в кредит");
        paymentPage.fillCard(card).submit();
        String actualNotification = paymentPage.captureResult("approved-credit-payment");

        DbHelper.CreditRecord credit = dbHelper.waitForNewCredit(knownIds);
        assertNotNull(credit, "В credit_request_entity не появилась новая запись");
        assertEquals("APPROVED", credit.getStatus());
        assertFalse(dbHelper.hasCardDataColumns("credit_request_entity"), "В таблице кредитных заявок есть столбец с данными карты");
        paymentPage.shouldShowResult(actualNotification, "Операция одобрена Банком.");
    }

    @Test
    void creditDeclinedRequestIsSavedToDatabase() throws SQLException, InterruptedException, IOException {
        CardData card = DataHelper.declinedCard();
        Set<String> knownIds = dbHelper.creditIds();

        PaymentPage paymentPage = tourPage.choosePaymentMethod("Купить в кредит");
        paymentPage.fillCard(card).submit();
        String actualNotification = paymentPage.captureResult("declined-credit-payment");

        DbHelper.CreditRecord credit = dbHelper.waitForNewCredit(knownIds);
        assertNotNull(credit, "Отказанная заявка отсутствует в credit_request_entity");
        assertEquals("DECLINED", credit.getStatus());
        assertFalse(dbHelper.hasCardDataColumns("credit_request_entity"), "В таблице кредитных заявок есть столбец с данными карты");
        paymentPage.shouldShowResult(actualNotification, "Ошибка! Банк отказал в проведении операции.");
    }
}
