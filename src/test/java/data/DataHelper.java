package data;

import com.github.javafaker.Faker;
import lombok.Value;

public class DataHelper {

    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getVasya() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getInvalidPassword(AuthInfo authInfo) {
        return new AuthInfo(authInfo.getLogin(), "wrong_password");
    }

    public static AuthInfo getInvalidLogin() {
        Faker faker = new Faker();
        return new AuthInfo(faker.name().username(), "qwerty123");
    }

    public static String getInvalidVerificationCode() {
        return String.format("%06d", new java.util.Random().nextInt(999999));
    }
}