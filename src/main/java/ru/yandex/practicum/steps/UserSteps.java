package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.config.Endpoints;
import ru.yandex.practicum.models.User;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class UserSteps {

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .body(user)
                .when()
                .post(Endpoints.USER_REGISTER)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(User user) {
        return given()
                .body(user)
                .when()
                .post(Endpoints.USER_LOGIN)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(String token) {
        if (token != null) {
            given()
                    .header("Authorization", token)
                    .when()
                    .delete(Endpoints.USER_DATA)
                    .then()
                    .statusCode(SC_ACCEPTED);

        }
    }
}