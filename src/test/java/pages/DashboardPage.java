package pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    public void shouldBeVisible() {
        $("h2").shouldBe(Condition.visible).shouldHave(Condition.text("Личный кабинет"));
    }
}