package pages;

import com.codeborne.selenide.Condition;
import data.DataHelper;

import static com.codeborne.selenide.Selenide.*;

public class LoginPage {
    public VerificationPage validLogin(DataHelper.AuthInfo authInfo) {
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();
        return new VerificationPage();
    }

    public void invalidLogin(DataHelper.AuthInfo authInfo) {
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();
    }

    public void shouldShowError(String message) {
        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(message));
    }

    public void shouldBeBlocked() {
        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Пользователь заблокирован"));
    }
}