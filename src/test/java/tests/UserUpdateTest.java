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
import static org.hamcrest.Matchers.*;

public class UserUpdateTest extends BaseApiTest {
    private UserClient userClient;
    private User existingUser;
    private String accessToken;

    @Before
    @Step("Подготовка тестовых данных")
    public void setUp() {
        userClient = new UserClient();
        existingUser = generateRandomUser();
        Response registerResponse = userClient.register(existingUser);
        accessToken = registerResponse.path("accessToken");
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка обновления почты авторизованного пользователя")
    public void testUpdateEmailWithAuth() {
        User updatedUser = new User("newemail@test.com", existingUser.getPassword(), existingUser.getName());

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

    }

    @Test
    @DisplayName("Проверка обновления имени авторизованного пользователя")
    public void testUpdateNameWithAuth() {
        User updatedUser = new User(existingUser.getEmail(), existingUser.getPassword(), "New Name");

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(existingUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Проверка обновления пароля авторизованного пользователя")
    public void testUpdatePasswordWithAuth() {
        User updatedUser = new User(existingUser.getEmail(), "newpassword123", existingUser.getName());

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        // Проверяем, что с новым паролем можно авторизоваться
        UserIdentity credentials = new UserIdentity(existingUser.getEmail(), "newpassword123");
        Response loginResponse = userClient.login(credentials);
        loginResponse.then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Проверка обновления почты без авторизации")
    public void testUpdateEmailWithoutAuth() {
        User updatedUser = new User("newemail@test.com", existingUser.getPassword(), existingUser.getName());

        Response response = userClient.updateUser(updatedUser, "");

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Проверка обновления имени без авторизации")
    public void testUpdateNameWithoutAuth() {
        User updatedUser = new User(existingUser.getEmail(), existingUser.getPassword(), "New Name");

        Response response = userClient.updateUser(updatedUser, null);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Проверка обновления с неверным токеном")
    public void testUpdateWithInvalidToken() {
        User updatedUser = new User("newemail@test.com", existingUser.getPassword(), "New Name");

        Response response = userClient.updateUser(updatedUser, "invalid-token");

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
