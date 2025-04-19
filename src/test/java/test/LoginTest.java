package test;

import com.codeborne.selenide.Condition;
import data.DataHelper;
import db.DbUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

import org.junit.jupiter.api.BeforeAll;

import static com.codeborne.selenide.Selenide.*;

public class LoginTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide()
            .screenshots(true)
            .savePageSource(true));
    }

    @BeforeEach
    void setup() {
        DbUtils.cleanDatabase();
        DbUtils.insertDemoUser();
        open("http://localhost:9999");
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        var authInfo = DataHelper.getVasya();

        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        var code = db.DbUtils.waitForVerificationCode();

        $("[data-test-id=code] input").setValue(code);
        $("[data-test-id=action-verify]").click();

        $("h2").shouldHave(Condition.text("Личный кабинет")).shouldBe(Condition.visible);
    }

    @Test
    void shouldShowErrorIfPasswordInvalid() {
        var invalidInfo = DataHelper.getInvalidPassword(DataHelper.getVasya());

        $("[data-test-id=login] input").setValue(invalidInfo.getLogin());
        $("[data-test-id=password] input").setValue(invalidInfo.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldShowErrorIfLoginInvalid() {
        var invalidInfo = DataHelper.getInvalidLogin();

        $("[data-test-id=login] input").setValue(invalidInfo.getLogin());
        $("[data-test-id=password] input").setValue(invalidInfo.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldShowErrorIfVerificationCodeInvalid() {
        var authInfo = DataHelper.getVasya();

        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        var wrongCode = DataHelper.getInvalidVerificationCode();

        $("[data-test-id=code] input").setValue(wrongCode);
        $("[data-test-id=action-verify]").click();

        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Неверно указан код! Попробуйте ещё раз."));
    }

    @Test
    void shouldBlockUserAfterThreeFailedLoginAttempts() {
        var authInfo = DataHelper.getVasya();
        var invalidInfo = DataHelper.getInvalidPassword(authInfo);

        for (int i = 0; i < 3; i++) {
            open("http://localhost:9999");
            $("[data-test-id=login] input").setValue(invalidInfo.getLogin());
            $("[data-test-id=password] input").setValue(invalidInfo.getPassword());
            $("[data-test-id=action-login]").click();

            $("[data-test-id=error-notification] .notification__content")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
        }

        open("http://localhost:9999");
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Пользователь заблокирован"));
    }
}