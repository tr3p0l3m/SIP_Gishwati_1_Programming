// MainController.java
package Controller;

import Controller.Database.DatabaseController;
import Model.Patient;
import java.io.*;

public class MainController {

    public static void checkStorage() {
        File file = new File("user-store.txt");
        if (!file.exists()) {
            System.out.println("Welcome to Life Prognosis App! Initializing application...");
            runCommand("echo 'admin,admin' > user-store.txt");
            System.out.println("Initialization complete");
        }
    }

    public static String userInput(String message) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(message);
        String input = null;
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static String runCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        String output = "";
        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static double lifeExpectancy(int lifespan, int age, int yearsWithoutMedication) {
        return Math.ceil(((lifespan - age) * Math.pow(0.9, yearsWithoutMedication)));
    }

    public static String generateUUID() {
        String prefix = "LPT";
        DatabaseController cmd = new DatabaseController();
        String uuid = runCommand(cmd.getLineCount("user-store.txt"));
        if (uuid.length() < 5) {
            int zeros = 5 - uuid.length();
            for (int i = 0; i < zeros; i++) {
                uuid = "0" + uuid;
            }
        }
        return prefix + uuid;
    }

    public static boolean login(String username, String password) {
        System.out.println("Please login to continue");
        String[] userStore = runCommand("cat user-store.txt").split("\n");
        for (String user : userStore) {
            String[] userCredentials = user.split(",");
            if (userCredentials[0].equals(username) && userCredentials[1].equals(password)) {
                System.out.println("Login successful");
                return true;
            }
        }
        System.out.println("Login failed");
        return false;
    }

    public static Patient initiatePatientProfile() {
        Patient patient = new Patient(null, null, null, 0, null, null, null, false, null, false, null, 0);
        patient.set_email(userInput("Enter patient's email: "));
        patient.set_uuid(generateUUID());

        // Save the new patient profile (email, UUID) to user-store.txt
        runCommand("echo '" + patient.get_email() + "," + patient.get_uuid() + "' >> user-store.txt");

        System.out.println("Patient profile initiated. UUID generated: " + patient.get_uuid());
        return patient;
    }

    public static Patient completePatientProfile(Patient patient) {
		// Prompt for personal details only
		patient.setFirstName(userInput("Enter first name: "));
		patient.setLastName(userInput("Enter last name: "));
		patient.setUsername(userInput("Enter username: "));
		patient.setAge(safeParseInt(userInput("Enter age: ")));
		patient.setDob(userInput("Enter date of birth: "));
		String password = userInput("Set your password: ");  // Prompt for the password
	
		// Construct the new line with updated details
		String updatedLine = patient.get_email() + "," + patient.get_uuid() + "," + patient.getFirstName() + ","
				+ patient.getLastName() + "," + patient.getUsername() + "," + patient.getAge() + ","
				+ patient.getDob() + "," + password + "," + (patient.is_hiv_positive() ? "true" : "false") + ","
				+ (patient.get_diagnosis_date() != null ? patient.get_diagnosis_date() : "") + ","
				+ (patient.is_on_antiretroviral_therapy() ? "true" : "false") + ","
				+ (patient.get_medication_start_date() != null ? patient.get_medication_start_date() : "") + ","
				+ (patient.get_years_without_medication() > 0 ? patient.get_years_without_medication() : 0);
	
		// Update the user's line in the user-store.txt file
		runCommand("sed -i 's/" + patient.get_email() + "," + patient.get_uuid() + ".*/" + updatedLine
				+ "/' user-store.txt");
	
		// Notify the user that the profile has been completed
		System.out.println("Profile completed successfully. Please log in with your credentials.");
	
		// Keep prompting for login until successful
		boolean loggedIn = false;
		while (!loggedIn) {
			String username = userInput("Enter username: ");
			String enteredPassword = userInput("Enter password: ");
			if (login(username, enteredPassword,"patient")) {
				System.out.println("Login successful! Welcome back.");
				loggedIn = true; // Exit the loop if login is successful
			} else {
				System.out.println("Login failed. Please check your credentials and try again.");
			}
		}
		
		return patient;
	}
	
	

	

    public static void main(String[] args) {
		checkStorage();
	
		while (true) {
			System.out.println("1. Admin Login\n2. Complete Profile");
			String choice = userInput("Choose an option: ");
	
			if ("1".equals(choice)) {
				String username = userInput("Enter admin username: ");
				String password = userInput("Enter admin password: ");
				if (login(username, password, "admin")) {
					System.out.println("You are now logged in as admin.");
					initiatePatientProfile();
					// After initiating patient profile, return to the main menu
					continue;
				}
			} else if ("2".equals(choice)) {
				String uuid = userInput("Enter UUID: ");
				String[] userStore = runCommand("cat user-store.txt").split("\n");
				boolean found = false;
				for (String user : userStore) {
					String[] userDetails = user.split(",");
					if (userDetails.length > 1 && userDetails[1].equals(uuid)) {
						found = true;
	
						// Initialize patient with data
						String firstName = userDetails.length > 2 ? userDetails[2] : "";
						String lastName = userDetails.length > 3 ? userDetails[3] : "";
						String username = userDetails.length > 4 ? userDetails[4] : "";
						int age = userDetails.length > 5 ? safeParseInt(userDetails[5]) : 0;
						String dob = userDetails.length > 6 ? userDetails[6] : "";
						boolean hiv_positive = userDetails.length > 7 && Boolean.parseBoolean(userDetails[7]);
						String diagnosis_date = userDetails.length > 8 ? userDetails[8] : "";
						boolean on_antiretroviral_therapy = userDetails.length > 9 && Boolean.parseBoolean(userDetails[9]);
						String medication_start_date = userDetails.length > 10 ? userDetails[10] : "";
						int years_without_medication = userDetails.length > 11 ? safeParseInt(userDetails[11]) : 0;
	
						Patient patient = new Patient(firstName, lastName, username, age, dob, userDetails[0],
								userDetails[1], hiv_positive, diagnosis_date, on_antiretroviral_therapy,
								medication_start_date, years_without_medication);
						patient = completePatientProfile(patient);
						// After completing patient profile, return to the main menu
						continue;
					}
				}
				if (!found) {
					System.out.println("Invalid UUID. Please try again.");
				}
			} else {
				System.out.println("Invalid option. Please choose 1 or 2.");
			}
		}
	}
	
	



	public static boolean login(String username, String password, String role) {
		System.out.println("Please login to continue");
	
		String[] userStore = runCommand("cat user-store.txt").split("\n");
		
		if ("admin".equalsIgnoreCase(role)) {
			// Admin login
			for (String user : userStore) {
				String[] userCredentials = user.split(",");
				if (userCredentials.length >= 2 && userCredentials[0].equals(username) && userCredentials[1].equals(password)) {
					System.out.println("Admin login successful");
					return true;
				}
			}
			System.out.println("Admin login failed");
			return false;
		}
	
		if ("patient".equalsIgnoreCase(role)) {
			// Patient login
			for (String user : userStore) {
				String[] userDetails = user.split(",");
				// Ensure there are enough details for login validation
				if (userDetails.length >= 8 && userDetails[4].equals(username) && userDetails[7].equals(password)) {
					System.out.println("Patient login successful");
					return true;
				}
			}
			System.out.println("Patient login failed");
			return false;
		}
	
		System.out.println("Invalid role specified");
		return false;
	}

	
	// Helper method to safely parse integers
	private static int safeParseInt(String value) {
		if (value == null || value.trim().isEmpty()) {
			System.err.println("Error parsing integer: Value is empty or null");
			return 0;  // Return a default value or handle as needed
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			System.err.println("Error parsing integer: " + value);
			return 0;  // Return a default value or handle as needed
		}
	}
}
