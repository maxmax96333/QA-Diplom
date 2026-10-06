package ru.netology.diploma.page;

import io.qameta.allure.Step;
import io.qameta.allure.Allure;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.netology.diploma.data.CardData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    @Step("Заполнить данные банковской карты")
    public PaymentPage fillCard(CardData card) {
        $("input[placeholder='0000 0000 0000 0000']").setValue(card.getNumber());
        $("input[placeholder='08']").setValue(card.getMonth());
        $("input[placeholder='22']").setValue(card.getYear());
        $$(".input").findBy(text("Владелец")).$("input").setValue(card.getHolder());
        $("input[placeholder='999']").setValue(card.getCvc());
        return this;
    }

    @Step("Отправить данные карты")
    public void submit() {
        $(byText("Продолжить")).click();
    }

    @Step("Проверить сообщение с результатом операции")
    public String captureResult(String screenshotName) throws IOException {
        String actualNotification = $(".notification_visible").shouldBe(visible).getText();
        byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);

        Path screenshotPath = Paths.get("build", "verification-screenshots", screenshotName + ".png");
        Files.createDirectories(screenshotPath.getParent());
        Files.write(screenshotPath, screenshot);
        Allure.addAttachment("Сообщение приложения", "image/png",
                new ByteArrayInputStream(screenshot), ".png");
        return actualNotification;
    }

    @Step("Сверить сообщение с ожидаемым результатом операции")
    public void shouldShowResult(String actualNotification, String expectedMessage) {
        org.junit.jupiter.api.Assertions.assertTrue(actualNotification.contains(expectedMessage),
                "Ожидалось сообщение '" + expectedMessage + "', фактически показано: '" + actualNotification + "'");
    }
}
