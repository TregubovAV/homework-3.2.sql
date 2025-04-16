package test;

import com.codeborne.selenide.Configuration;
import data.DataGenerator.RegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static data.DataGenerator.*;

public class DeliveryTest {

    @BeforeEach
    void setup() {
        Configuration.headless = false;
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
        $("[data-test-id=login] input").setValue("invalidLogin");
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible);
    }

    @Test
    void shouldShowErrorIfPasswordInvalid() {
        RegistrationDto user = createActiveUser();
        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue("invalidPassword");
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible);
    }

    @Test
    void shouldRewriteUserWithSameLogin() {
        String reusedLogin = "someUser";
        RegistrationDto firstUser = new RegistrationDto(reusedLogin, "firstPassword", "active");
        RegistrationDto secondUser = new RegistrationDto(reusedLogin, "secondPassword", "active");

        registerUser(firstUser);  // регистрируем через метод из DataGenerator
        registerUser(secondUser); // перезаписываем

        open("http://localhost:9999");
        $("[data-test-id=login] input").setValue(secondUser.getLogin());
        $("[data-test-id=password] input").setValue(secondUser.getPassword());
        $("[data-test-id=action-login]").click();
        $("h2").shouldHave(text("Личный кабинет")).shouldBe(visible);
    }
}