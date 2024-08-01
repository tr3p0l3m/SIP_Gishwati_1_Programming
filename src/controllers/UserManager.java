package controllers;

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

    // Add any other common methods for managing users
}
