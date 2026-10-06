package ru.netology.diploma.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TourPage {
    @Step("Открыть страницу покупки тура")
    public void open() {
        Selenide.open(System.getProperty("sut.url", "http://host.docker.internal:8080"));
        $(byText("Путешествие дня")).shouldBe(visible);
    }

    @Step("Выбрать способ оплаты: {method}")
    public PaymentPage choosePaymentMethod(String method) {
        $(byText(method)).click();
        return new PaymentPage();
    }
}
