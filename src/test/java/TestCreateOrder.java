import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Order;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.OrderSteps;
import ru.yandex.practicum.steps.UserSteps;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class TestCreateOrder extends BaseTest {
    private UserSteps userSteps = new UserSteps();
    private OrderSteps orderSteps = new OrderSteps();
    private String accessToken;
    private List<String> ingredients;

    private User user;
    private Faker faker = new Faker();

    @Before
    public void setUp() {
        user = new User();
        user.withName(faker.name().username())
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password());
        accessToken = userSteps.createUser(user).extract().path("accessToken");
        ingredients = orderSteps.getIngredients()
                .extract().path("data._id");
    }

    @Test
    @DisplayName("Создать заказ с авторизацией")
    public void createOrderWithAuth() {
        Order order = new Order(List.of(ingredients.get(0), ingredients.get(1)));
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создать заказ без авторизации")
    public void createOrderWithoutAuth() {
        Order order = new Order(List.of(ingredients.get(0), ingredients.get(1)));
        orderSteps.createOrder(order, null)
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создать заказ без ингредиентов")
    public void createEmptyOrder() {
        Order order = new Order(List.of());
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создать заказ с неверным хешем")
    public void createOrderWrongHash() {
        Order order = new Order(List.of("Hash228"));
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
    @After
    public void tearDown() {
        userSteps.deleteUser(accessToken);
    }
}