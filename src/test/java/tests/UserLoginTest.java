package tests;

import api.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import pojo.User;
import pojo.UserIdentity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.BaseApiTest;

import static random.UserGenerator.generateRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseApiTest {
    private UserClient userClient;
    private User existingUser;
    private String accessToken;

    @Before
    @Step("Подготовка тестовых данных")
    public void setUp() {
        userClient = new UserClient();
        // Создание тестового пользователя для тестов
        existingUser = generateRandomUser();
        userClient.register(existingUser);
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        // Удаление тестового пользователя
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка успешной авторизации уже существующего пользователя")
    public void testSuccessLogin() {
        UserIdentity credentials = new UserIdentity(existingUser.getEmail(), existingUser.getPassword());

        Response response = userClient.login(credentials);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()))
                .body("user.email", equalTo(existingUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(existingUser.getName()));

        // Сохранение токена, что бы потом удалить пользука
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Проверка авторизации с неверной почтой")
    public void testWrongEmail() {
        UserIdentity credentials = new UserIdentity("wrong@email.com", existingUser.getPassword());

        Response response = userClient.login(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации с неверным паролем")
    public void testWrongPassword() {
        UserIdentity credentials = new UserIdentity(existingUser.getEmail(), "wrong_password");

        Response response = userClient.login(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации без почты")
    public void testWithoutEmail() {
        UserIdentity credentials = new UserIdentity("", existingUser.getPassword());

        Response response = userClient.login(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации без пароля")
    public void testWithoutPassword() {
        UserIdentity credentials = new UserIdentity(existingUser.getEmail(), "");

        Response response = userClient.login(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
