package api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.User;
import pojo.UserIdentity;

import static stellarburgers.BaseApiTest.getBaseSpec;
import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String DELETE_URL = "/api/auth/user";
    private static final String UPDATE_URL = "/api/auth/user";

    public Response register(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_URL);
    }
    public Response login(UserIdentity credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(LOGIN_URL);
    }
    public Response delete(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(DELETE_URL);
    }
    public Response updateUser(User user, String accessToken) {
        RequestSpecification request = given()
                .spec(getBaseSpec())
                .body(user);

        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", accessToken);
        }

        return request.when()
                .patch(UPDATE_URL);
    }
}
