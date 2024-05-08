package roomescape.domain;

public class User {

    private final String name;
    private final String email;
    private final String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public boolean isEmailMatches(String email) {
        return this.email.equals(email);
    }

    public boolean isPasswordMatches(String password) {
        return this.password.equals(password);
    }

    public String getName() {
        return name;
    }
}
