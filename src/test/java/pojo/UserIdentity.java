package pojo;

public class UserIdentity {
    private String email;
    private String password;

    public UserIdentity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
