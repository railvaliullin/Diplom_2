package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import pojo.User;
import org.junit.Test;
import stellarburgers.BaseApiTest;

import static random.UserGenerator.generateRandomUser;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserRegistrationTest extends BaseApiTest {
    @Test
    @DisplayName("Проверка успешной регистрации уникального пользователя")
    public void testSuccessfulRegistration() {
        User user = generateRandomUser();

        Response response = userClient.register(user);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));

        System.out.println("Пользователь успешно зарегистрирован: " + user.getEmail());
    }

    @Test
    @DisplayName("Проверка регистрации существующего пользователя")
    public void testRegisterExistingUser() {
        User user = generateRandomUser();
        userClient.register(user); // Регистрации в первый раз

        Response response = userClient.register(user); // Регистрация второй раз

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

        System.out.println("Попытка повторной регистрации: " + user.getEmail());
    }

    @Test
    @DisplayName("Проверка регистрации без почты")
    public void testRegisterWithoutEmail() {
        User user = new User("", "password", "Name");

        Response response = userClient.register(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

        System.out.println("Попытка регистрации без почты");
    }

    @Test
    @DisplayName("Проверка регистрации без пароля")
    public void testRegisterWithoutPassword() {
        User user = new User("email@test.com", "", "Name");

        Response response = userClient.register(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

        System.out.println("Попытка регистрации без пароля");
    }

    @Test
    @DisplayName("Проверка регистрации без имени")
    public void testRegisterWithoutName() {
        User user = new User("email@test.com", "password", "");

        Response response = userClient.register(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

        System.out.println("Попытка регистрации без имени");
    }
}
