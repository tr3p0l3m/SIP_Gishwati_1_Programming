package controllers;

public class AdminController {

    public void registerPatient(String email) {
        String command = "../../scripts/user-manager.sh register_patient " + email;
        String output = UserManager.executeBashCommand(command);
        System.out.println(output);
    }

    public void initializeAdmin(String email) {
        String command = "../../scripts/user-manager.sh initialize_admin " + email;
        String output = UserManager.executeBashCommand(command);
        System.out.println(output);
    }

    // Other Admin-specific operations
}
