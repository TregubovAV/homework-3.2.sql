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

    public static AuthInfo getInvalidPassword(AuthInfo authInfo) {
        return new AuthInfo(authInfo.getLogin(), "wrong_password");
    }

    public static AuthInfo getInvalidLogin() {
        return new AuthInfo("nonexistent", "qwerty123");
    }
}