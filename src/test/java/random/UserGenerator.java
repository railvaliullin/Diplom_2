package random;

import pojo.User;

public class UserGenerator {
    public static User generateRandomUser() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "test" + timestamp + "@test.com",
                "password" + timestamp,
                "name" + timestamp
        );
    }
}
