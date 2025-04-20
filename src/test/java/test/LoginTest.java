package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import db.DbUtils;
import org.junit.jupiter.api.*;

import io.qameta.allure.selenide.AllureSelenide;
import pages.LoginPage;
import pages.DashboardPage;

import static com.codeborne.selenide.Selenide.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest {

    @BeforeAll
    void beforeAll() {
        SelenideLogger.addListener("allure", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @AfterAll
    void afterAll() {
        DbUtils.cleanDatabase();
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getVasya();
        var verificationPage = loginPage.validLogin(authInfo);
        var code = db.DbUtils.waitForVerificationCode();
        verificationPage.validVerify(code);
        new DashboardPage().shouldBeVisible();
    }

    @Test
    void shouldShowErrorIfPasswordInvalid() {
        var loginPage = new LoginPage();
        var invalidInfo = DataHelper.getInvalidPassword(DataHelper.getVasya());
        loginPage.invalidLogin(invalidInfo);
    }

    @Test
    void shouldShowErrorIfLoginInvalid() {
        var loginPage = new LoginPage();
        var invalidInfo = DataHelper.getInvalidLogin();
        loginPage.invalidLogin(invalidInfo);
    }

    @Test
    void shouldShowErrorIfVerificationCodeInvalid() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getVasya();
        var verificationPage = loginPage.validLogin(authInfo);
        var wrongCode = DataHelper.getInvalidVerificationCode();
        verificationPage.invalidVerify(wrongCode);
    }

    @Test
    void shouldBlockUserAfterThreeFailedLoginAttempts() {
        var authInfo = DataHelper.getVasya();
        var invalidInfo = DataHelper.getInvalidPassword(authInfo);

        for (int i = 0; i < 3; i++) {
            open("http://localhost:9999");
            var loginPage = new LoginPage();
            loginPage.invalidLogin(invalidInfo);
        }

        open("http://localhost:9999");
        var loginPage = new LoginPage();
        loginPage.invalidLogin(authInfo); // теперь пользователь должен быть заблокирован
        loginPage.shouldBeBlocked();
    }
}