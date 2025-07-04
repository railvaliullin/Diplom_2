package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.*;
import api.UserClient;
import api.OrderClient;
import stellarburgers.BaseApiTest;

import static random.IngredientGenerator.getValidIngredients;
import static random.UserGenerator.generateRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreateTest extends BaseApiTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private User testUser;

    @Before
    @Step("Подготовка тестовых данных")
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        testUser = generateRandomUser();
        Response registerResponse = userClient.register(testUser);
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
    @DisplayName("Проверка создания заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        String[] ingredients = getValidIngredients();
        Order order = new Order(ingredients);

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", not(empty()));
    }

    @Test
    @DisplayName("Проверка создания заказа без авторизации, но с ингредиентами")
    public void testCreateOrderWithoutAuthWithIngredients() {
        String[] ingredients = getValidIngredients();
        Order order = new Order(ingredients);

        Response response = orderClient.createOrder(order, "");

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией, но без ингредиентов")
    public void testCreateOrderWithAuthWithoutIngredients() {
        Order order = new Order(new String[]{});

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка создания заказа без авторизации и без ингредиентов")
    public void testCreateOrderWithoutAuthAndIngredients() {
        Order order = new Order(new String[]{});

        Response response = orderClient.createOrder(order, "");

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка создания заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        Order order = new Order(new String[]{"invalidIngredientHash"});

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
