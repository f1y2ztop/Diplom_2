import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.UserSteps;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;


public class TestCreateUser extends BaseTest {
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
    }

    @Test
    @DisplayName("Регистрация пользователя")
    public void addNewUser() {
        ValidatableResponse response = userSteps.createUser(user);
                response.statusCode(SC_OK)
                        .body("success", is(true));
                accessToken = response.extract().path("accessToken");

    }

    @Test
    @DisplayName("Регистрация пользователя, который уже зарегистрирован")
    public void addTheSameUser() {
        ValidatableResponse response = userSteps.createUser(user);
            response.statusCode(SC_OK);
        accessToken = response.extract().path("accessToken");
        userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Регистрация с пустым полем имя")
    public void addUserWithoutName() {
        user.withName("");
        ValidatableResponse response = userSteps.createUser(user);
            response.statusCode(SC_FORBIDDEN)
                    .body("success", is(false))
                    .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация с пустым полем Email")
    public void addUserWithoutEmail() {
        user.withEmail("");
        ValidatableResponse response = userSteps.createUser(user);
            response.statusCode(SC_FORBIDDEN)
                    .body("success", is(false))
                    .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация с пустым полем Password")
    public void addUserWithoutPassword() {
        user.withPassword("");
        ValidatableResponse response = userSteps.createUser(user);
            response.statusCode(SC_FORBIDDEN)
                    .body("success", is(false))
                    .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        userSteps.deleteUser(accessToken);
    }
}
