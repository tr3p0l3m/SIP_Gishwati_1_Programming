import utilis.UserRole;

public abstract class User {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected String uuid;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public abstract UserRole getRole();

    // Getters and setters can be added here as needed
}
