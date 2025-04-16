package data;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import lombok.Value;

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

    @Value
    public static class RegistrationDto {
        String login;
        String password;
        String status;
    }

    public static RegistrationDto generateUser(String status) {
        return new RegistrationDto(
                faker.name().username(),
                faker.internet().password(),
                status
        );
    }

    public static RegistrationDto createUser(String status) {
        RegistrationDto user = generateUser(status);
        registerUser(user);
        return user;
    }

    public static RegistrationDto createActiveUser() {
        return createUser("active");
    }

    public static RegistrationDto createBlockedUser() {
        return createUser("blocked");
    }

    public static void registerUser(RegistrationDto user) {
        given()
            .spec(requestSpec)
            .body(user)
        .when()
            .post("/api/system/users")
        .then()
            .statusCode(200);
    }
}