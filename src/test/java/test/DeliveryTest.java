package test;

import data.DataGenerator.RegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static data.DataGenerator.*;

public class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSuccessfullyLoginWithActiveUser() {
        RegistrationDto user = createActiveUser();
        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();
        $("h2").shouldHave(text("Личный кабинет")).shouldBe(visible);
    }

    @Test
    void shouldNotLoginWithBlockedUser() {
        RegistrationDto user = createBlockedUser();
        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldHave(text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldShowErrorIfLoginInvalid() {
        RegistrationDto user = createActiveUser();
        $("[data-test-id=login] input").setValue(getRandomLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(visible)
            .shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldShowErrorIfPasswordInvalid() {
        RegistrationDto user = createActiveUser();
        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(getRandomPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(visible)
            .shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldRewriteUserWithSameLogin() {
        String reusedLogin = getRandomLogin();
        String password1 = getRandomPassword();
        String password2 = getRandomPassword();

        RegistrationDto firstUser = generateUserWithLogin(reusedLogin, password1, "active");
        RegistrationDto secondUser = generateUserWithLogin(reusedLogin, password2, "active");

        registerUser(firstUser);
        registerUser(secondUser);

        open("http://localhost:9999");
        $("[data-test-id=login] input").setValue(secondUser.getLogin());
        $("[data-test-id=password] input").setValue(secondUser.getPassword());
        $("[data-test-id=action-login]").click();
        $("h2").shouldHave(text("Личный кабинет")).shouldBe(visible);
    }
}