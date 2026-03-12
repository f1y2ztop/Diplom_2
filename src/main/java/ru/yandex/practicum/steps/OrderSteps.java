package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.config.Endpoints;
import ru.yandex.practicum.models.Order;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredients() {
        return given()
                .get(Endpoints.INGREDIENTS)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String token) {
        var request = given().body(order);
        if (token != null) {
            request.header("Authorization", token);
        }
        return request
                .when()
                .post(Endpoints.ORDERS)
                .then();
    }
}
