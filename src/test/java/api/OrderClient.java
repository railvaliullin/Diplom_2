package api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.Order;

import static io.restassured.RestAssured.given;
import static stellarburgers.BaseApiTest.getBaseSpec;

public class OrderClient {
    private static final String ORDERS_URL = "/api/orders";

    public Response createOrder(Order order, String accessToken) {
        RequestSpecification request = given()
                .spec(getBaseSpec())
                .body(order);

        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", accessToken);
        }

        return request.when()
                .post(ORDERS_URL);
    }
    public Response getOrders(String accessToken) {
        RequestSpecification request = given()
                .spec(getBaseSpec());

        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", accessToken);
        }

        return request.when()
                .get(ORDERS_URL);
    }
}
