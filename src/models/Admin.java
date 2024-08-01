package models;

public class Admin extends User {

    public Admin(String email, String password) {
        super(email, password, UserRole.ADMIN);
    }

    // Additional Admin-specific methods if needed
}
