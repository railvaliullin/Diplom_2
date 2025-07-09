package tests;

import api.OrderClient;
import api.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import pojo.Order;
import pojo.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.BaseApiTest;

import static random.IngredientGenerator.getValidIngredients;
import static random.UserGenerator.generateRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class OrderGetTest extends BaseApiTest {
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

        String[] ingredients = getValidIngredients();
        Order order = new Order(ingredients);
        orderClient.createOrder(order, accessToken);
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка получения заказов с авторизованным пользователем")
    public void testGetOrdersWithAuth() {
        Response response = orderClient.getOrders(accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders[0].ingredients", not(empty()))
                .body("orders[0].status", not(emptyString()))
                .body("orders[0].name", not(emptyString()))
                .body("orders[0].createdAt", not(emptyString()))
                .body("orders[0].updatedAt", not(emptyString()))
                .body("total", greaterThan(0))
                .body("totalToday", greaterThan(0));
    }

    @Test
    @DisplayName("Проверка получения заказов с неавторизованным пользователем")
    public void testGetOrdersWithoutAuth() {
        Response response = orderClient.getOrders("");

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
