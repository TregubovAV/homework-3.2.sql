package db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.UUID;

public class DbUtils {
    private static final QueryRunner runner = new QueryRunner();
    private static final BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl("jdbc:mysql://localhost:3306/app");
        ds.setUsername("app");
        ds.setPassword("pass");
    }

    public static void cleanDatabase() {
        try (var conn = ds.getConnection()) {
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM cards;");
            runner.update(conn, "DELETE FROM users;");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке базы данных", e);
        }
    }

    public static void insertUser(String login, String password) {
        var userId = UUID.randomUUID().toString();
        var hashedPassword = "$2a$10$78IOiKiwJVg51m9f2HE1G.y8bLeaNVaDlwPDVr1lMvuGNVex/2Vka"; // пароль qwerty123
        var sql = "INSERT INTO users (id, login, password, status) VALUES (?, ?, ?, ?)";

        try (var conn = ds.getConnection()) {
            runner.update(conn, sql, userId, login, hashedPassword, "active");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке пользователя", e);
        }
    }

    public static String getLatestVerificationCode() {
        var sql = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
    
        try (var conn = ds.getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении кода", e);
        }
    }

    public static String waitForVerificationCode() {
        var retries = 10;
        var delayMillis = 500;

        for (int i = 0; i < retries; i++) {
            var code = getLatestVerificationCode();
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