import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.UserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class TestLoginUser extends BaseTest {
    private User user;
    private Faker faker = new Faker();
    private UserSteps userSteps = new UserSteps();
    private String accessToken;

    @Before
    public void SetUp() {
        user = new User();
        user.withName(faker.name().username())
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password());
        accessToken = userSteps.createUser(user).extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин пользователя")
    public void loginUser() {
        ValidatableResponse response = userSteps.loginUser(user);
                response.statusCode(SC_OK)
                        .body("success", is(true));

    }

    @Test
    @DisplayName("Логин пользователя с неверным полем email")
    public void loginWithWrongEmail() {
        user.withEmail("Fakemail@yandex.ru");
        ValidatableResponse response = userSteps.loginUser(user);
            response.statusCode(SC_UNAUTHORIZED)
                    .body("success", is(false))
                    .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя с неверным полем password")
    public void loginWithWrongPassword() {
        user.withPassword("ThisPasswordIsFake");
        ValidatableResponse response = userSteps.loginUser(user);
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        userSteps.deleteUser(accessToken);
    }
}
