package test;

import com.codeborne.selenide.Condition;
import data.DataHelper;
import db.DbUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;

public class LoginTest {

    @BeforeEach
    void setup() {
        DbUtils.cleanDatabase();
        DbUtils.insertDemoUser();
        open("http://localhost:9999");
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        var authInfo = DataHelper.getVasya();

        // Вводим логин и пароль
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        // Получаем код из базы (его генерирует само приложение после логина)
        var code = DbUtils.waitForVerificationCode();

        // Вводим код подтверждения
        $("[data-test-id=code] input").setValue(code);
        $("[data-test-id=action-verify]").click();

        // Проверяем, что перешли в Личный кабинет
        $("h2").shouldHave(Condition.text("Личный кабинет")).shouldBe(Condition.visible);
    }
}