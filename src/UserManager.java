// src/UserManager.java

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UserManager {
    public static String executeBashCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static void registerPatient(String email) {
        String command = "../scripts/user-manager.sh register_patient " + email;
        String output = executeBashCommand(command);
        System.out.println(output);
    }

    public static void completePatientRegistration(String uuid, String firstName, String lastName,
                                                   String dateOfBirth, boolean hasHIV, String diagnosisDate,
                                                   boolean onART, String artStartDate, String country, String password) {
        String command = "../scripts/user-manager.sh complete_registration " + uuid + " " + firstName + " " + lastName + " " +
                         dateOfBirth + " " + hasHIV + " " + diagnosisDate + " " + onART + " " + artStartDate + " " + country + " " + password;
        String output = executeBashCommand(command);
        System.out.println(output);
    }
}
