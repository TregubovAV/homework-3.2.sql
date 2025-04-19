package data;

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

    public static AuthInfo getInvalidPassword(AuthInfo validInfo) {
        return new AuthInfo(validInfo.getLogin(), "wrong_password");
    }

    public static AuthInfo getInvalidLogin() {
        return new AuthInfo("invalid_user", "qwerty123");
    }

    public static String getInvalidVerificationCode() {
        return "000000";
    }
}