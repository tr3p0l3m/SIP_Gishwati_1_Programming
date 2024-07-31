// src/Main.java
public class Main {
    public static void main(String[] args) {
        // Admin initializes the patient registration
        UserManager.registerPatient("patient@example.com");

        // Later, the patient completes the registration
        UserManager.completePatientRegistration("generated-uuid", "John", "Doe", "1990-01-01",
                                                true, "2022-05-01", true, "2022-06-01", "USA", "securePassword");
    }
}
