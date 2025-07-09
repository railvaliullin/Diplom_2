package stellarburgers;

import api.UserClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

import static io.restassured.http.ContentType.JSON;

public class BaseApiTest {
    protected static RequestSpecification spec;
    protected UserClient userClient = new UserClient();

    @BeforeClass
    public static void generalSetUp() {
        spec = new RequestSpecBuilder()
                .setBaseUri("https://stellarburgers.nomoreparties.site")
                .setContentType(JSON)
                .build();
    }

    public static RequestSpecification getBaseSpec() {
        return spec;
    }
}
