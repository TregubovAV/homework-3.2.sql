package db;

import java.sql.*;
import java.util.UUID;

public class DbUtils {

    public static void cleanDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth_codes;");
            stmt.executeUpdate("DELETE FROM card_transactions;");
            stmt.executeUpdate("DELETE FROM cards;");
            stmt.executeUpdate("DELETE FROM users;");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке базы данных", e);
        }
    }

    public static void insertDemoUser() {
        String userId = UUID.randomUUID().toString();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (id, login, password, status) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, userId);
            stmt.setString(2, "vasya");
            stmt.setString(3, "$2a$10$78IOiKiwJVg51m9f2HE1G.y8bLeaNVaDlwPDVr1lMvuGNVex/2Vka"); // пароль: qwerty123
            stmt.setString(4, "active");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке демо-пользователя", e);
        }
    }

    public static String getLatestVerificationCode() {
        String code = "";
        String query = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                code = rs.getString("code");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении кода", e);
        }

        return code;
    }

    public static String waitForVerificationCode() {
        String code = "";
        int retries = 10;
        int delayMillis = 500;

        for (int i = 0; i < retries; i++) {
            code = getLatestVerificationCode();
            if (!code.isEmpty()) {
                return code;
            }
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException("Ожидание кода было прервано", e);
            }
        }

        throw new RuntimeException("Код подтверждения так и не появился в базе");
    }
}