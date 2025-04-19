package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private final SelenideElement codeField = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");

    public DashboardPage validVerify(String code) {
        codeField.setValue(code);
        verifyButton.click();
        return new DashboardPage();
    }

    public void invalidVerify(String code) {
        codeField.setValue(code);
        verifyButton.click();
    }

    public void shouldSeeError() {
        $("[data-test-id=error-notification] .notification__content")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Ошибка! Неверно указан код! Попробуйте ещё раз."));
    }
}