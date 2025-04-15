package data;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final Faker faker = new Faker();
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(io.restassured.filter.log.LogDetail.ALL)
            .build();

    public static class RegistrationDto {
        private final String login;
        private final String password;
        private final String status;

        public RegistrationDto(String login, String password, String status) {
            this.login = login;
            this.password = password;
            this.status = status;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }
    }

    public static RegistrationDto createUser(String status) {
        RegistrationDto user = new RegistrationDto(
                faker.name().username(),
                faker.internet().password(),
                status
        );

        given()
            .spec(requestSpec)
            .body(user)
        .when()
            .post("/api/system/users")
        .then()
            .statusCode(200);

        return user;
    }

    public static RegistrationDto createActiveUser() {
        return createUser("active");
    }

    public static RegistrationDto createBlockedUser() {
        return createUser("blocked");
    }
}
