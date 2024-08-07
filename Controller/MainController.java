// MainController.java
package Controller;

import Model.Admin;
import Model.Patient;
import java.io.*;
import java.util.Arrays;

public class MainController {

	public static void checkStorage() {
		File file = new File("storage/user-store.txt");
		if (!file.exists()) {
			System.out.println("Welcome to Life Prognosis App! Initializing application...");
			Admin admin = new Admin("admin", "admin", "admin", 0, null, "admin");
			String adminDetails = admin.getFirstName() + "," + admin.getLastName() + "," + admin.getUsername() + ","
					+ admin.getAge() + "," + admin.getDOB() + "," + admin.getPassword();
			String cmd = "script/insert.sh " + adminDetails;
			executeCommand(cmd);
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

	public static double lifeExpectancy(String country, double age, int yearsWithoutMedication) {
		String[] countryStore;
		country = country.trim();
		countryStore = executeCommand("script/search.sh " + country + " storage/life-expectancy.csv").split(",");
		if (countryStore.length < 7) {
			System.out.println("Invalid " + country + " . Please try again.");
			return 0;
		}
		double lifespan = Double.parseDouble(countryStore[6]);
		return Math.ceil(((lifespan - age) * Math.pow(0.9, yearsWithoutMedication)));
	}

	public static String generateUUID() {
		String prefix = "LPT";
		String uuid = "";
		uuid = executeCommand("script/wordcount.sh");
		if (uuid.length() < 5) {
			int zeros = 5 - uuid.length();
			for (int i = 0; i < zeros; i++) {
				uuid = "0" + uuid;
			}
		}
		System.out.println("UUID: " + (prefix + uuid).trim());
		return (prefix + uuid).trim();
	}

	public static Patient initiatePatientProfile() {
		Patient patient = new Patient(null, null, null, 0, null, null, null, false, null, false, null, 0, null, null);
		patient.set_email(userInput("Enter patient's email: "));
		patient.set_uuid(generateUUID());

		String patientDetails = patient.getFirstName() + "," + patient.getLastName() + "," + patient.getUsername() + ","
				+ patient.getAge() + "," + patient.getDob() + "," + patient.get_email() + "," + patient.get_uuid() + ","
				+ patient.is_hiv_positive() + "," + patient.get_diagnosis_date() + ","
				+ patient.is_on_antiretroviral_therapy() + ","
				+ patient.get_medication_start_date() + "," + patient.get_years_without_medication() + ","
				+ patient.get_password() + "," + patient.get_country_of_residence();

		System.out.println(patientDetails);

		// Save the new patient profile (email, UUID) to user-store.txt
		executeCommand("script/insert.sh " + patientDetails);

		System.out.println("Patient profile initiated. UUID generated: " + patient.get_uuid());
		return patient;
	}

	public static Patient completePatientProfile(Patient patient, int line_number) {
		// Prompt for personal details only
		patient.setFirstName(userInput("Enter first name: "));
		patient.setLastName(userInput("Enter last name: "));
		patient.setUsername(userInput("Enter username: "));
		patient.setAge(safeParseInt(userInput("Enter age: ")));
		patient.setDob(userInput("Enter date of birth: "));
		patient.set_country_of_residence(getPatientCountryISO(userInput("Enter country of residence: ")));
		patient.set_password(userInput("Enter Password: ")); // Prompt for the password

		// tell patient that the next steps are optional and can be completed later. ask
		// if they want to continue
		String optional = userInput(
				"The next steps are optional and can be completed later. Do you want to continue? (Y/N): ");
		if (optional.equalsIgnoreCase("Y")) {
			// Prompt for medical details
			patient.set_hiv_positive(Boolean.parseBoolean(userInput("Are you HIV positive? (true/false): ")));
			patient.set_diagnosis_date(userInput("Enter diagnosis date: "));
			patient.set_on_antiretroviral_therapy(
					Boolean.parseBoolean(userInput("Are you on antiretroviral therapy? (true/false): ")));
			patient.set_medication_start_date(userInput("Enter medication start date: "));
			patient.set_years_without_medication(safeParseInt(userInput("Enter years without medication: ")));
		}

		// TODO: confirm password

		// Construct the new line with updated details
		String updatedLine = patient.getFirstName() + "," + patient.getLastName() + "," + patient.getUsername() + ","
				+ patient.getAge() + "," + patient.getDob() + "," + patient.get_email() + "," + patient.get_uuid() + ","
				+ patient.is_hiv_positive() + "," + patient.get_diagnosis_date() + ","
				+ patient.is_on_antiretroviral_therapy() + ","
				+ patient.get_medication_start_date() + "," + patient.get_years_without_medication() + ","
				+ patient.get_password() + "," + patient.get_country_of_residence();

		// Update the user's line in the user-store.txt file
		executeCommand("script/edit.sh " + line_number + " " + updatedLine.replace("/", "-"));// this produces an
																								// error.. fix it

		// Notify the user that the profile has been completed
		System.out.println("Profile completed successfully. Please log in with your credentials.");
		return patient;
	}

	public static void main(String[] args) {
		checkStorage();
		// Ask user to Login or Complete Profile
		String choice = userInput("Please choose: \n1.Login \n2.Complete Profile \n3.Quit: ");
		if (choice.equals("1")) {
			String username = userInput("Enter username: ");
			String password = userInput("Enter password: ");
			// login and continue if successful but retry if failed
			while (!login(username, password)) {
				username = userInput("Enter username: ");
				password = userInput("Enter password: ");
			}

			if (username.equals("admin")) {
				System.out.println("Welcome Admin");
				// ask admin to create a new patient profile, update patient profile or export
				// patient data
				String adminChoice = userInput(
						"Please choose: \n1.Create new patient profile \n2.Update patient profile \n3.Export patient data \n4.Export patient analytics \n5. Logout ");
				if (adminChoice.equals("1")) {
					initiatePatientProfile();
					System.out.println("Patient profile created successfully");
					main(args);
				} else if (adminChoice.equals("2")) {
					String patient_uuid = userInput("Enter patient uuid: ");
					Patient patient = getPatientDetails(patient_uuid);
					// update patient email
					String updatedEmail = userInput("Enter new email: ").trim();
					// replace the email in the user-store.txt with the updated email
					executeCommand("script/edit-email.sh " + patient.get_email() + " " + updatedEmail);
					System.out.println("Patient profile updated successfully");
					main(args);
				} else if (adminChoice.equals("3")) {
					// export patient data
					// TODO: remember to format the export
					executeCommand("script/export.sh " + "storage/user-store.txt" + " storage/patient-data.csv");
					System.out.println("Patient data exported successfully!");
					main(args);
				} else if (adminChoice.equals("4")) {
					// export patient data
					executeCommand("touch storage/patient-analytics.csv");
					System.out.println("Patient data exported successfully");
					main(args);
				} else if (adminChoice.equals("5")) {
					System.out.println("Admin logged out successfully");
					main(args);
				} else {
					System.out.println("Invalid choice");
					main(args);
				}
			} else {
				Patient patient = getPatientDetails(username);
				System.out.println("Welcome Patient " + patient.getFirstName());
				// Ask patient to view their profile, update their profile or delete their
				// profile
				String patientChoice = userInput(
						"Please choose: \n1.View patient profile \n2.Update patient profile \n3.Delete patient profile: ");
				if (patientChoice.equals("1")) {
					double lifeExpectancy = lifeExpectancy(patient.get_country_of_residence(),
							(double) patient.getAge(), patient.get_years_without_medication());
					System.out.println("Life Expectancy: " + lifeExpectancy);
					// output patient profile with nice formatting
					System.out.println(patient.getFirstName() + "'s Profile \n" + "First Name: "
							+ patient.getFirstName()
							+ "\nLast Name: " + patient.getLastName() + "\nUsername: " + patient.getUsername()
							+ "\nAge: " + patient.getAge() + "\nDate of Birth: " + patient.getDob() + "\nEmail: "
							+ patient.get_email() + "\nUUID: " + patient.get_uuid() + "\nHIV Positive: "
							+ patient.is_hiv_positive() + "\nDiagnosis Date: " + patient.get_diagnosis_date()
							+ "\nOn Antiretroviral Therapy: " + patient.is_on_antiretroviral_therapy()
							+ "\nMedication Start Date: " + patient.get_medication_start_date()
							+ "\nYears Without Medication: " + patient.get_years_without_medication()
							+ "\nLife Expectancy: " + lifeExpectancy);
				} else if (patientChoice.equals("2")) {
					String uuid = patient.get_uuid();
					String[] userStore = executeCommand("script/search.sh " + uuid + " storage/user-store.txt")
							.split(":");
					System.out.println(userStore[1]);
					if (userStore.length < 2) {
						System.out.println("Something Went Wrong. Please try again.");
						main(args);
					}
					int line_number = Integer.parseInt(userStore[0]);
					completePatientProfile(patient, line_number);
					System.out.println("Patient profile updated successfully");
					main(args);
				} else if (patientChoice.equals("3")) {
					System.out.println("Patient profile deleted successfully");
					main(args);
				} else {
					System.out.println("Invalid choice");
				}
			}
		} else if (choice.equals("2")) {
			String uuid = userInput("Enter UUID: ");
			String[] userStore = executeCommand("script/search.sh " + uuid + " storage/user-store.txt").split(":");
			if (userStore.length < 2) {
				System.out.println("Invalid UUID. Please try again.");
				main(args);
			}
			int line_number = Integer.parseInt(userStore[0]);
			String user = userStore[1];
			String[] userDetails = user.split(",");
			Patient patient = new Patient(userDetails[0], userDetails[1], userDetails[2],
					safeParseInt(userDetails[3]),
					userDetails[4], userDetails[5], userDetails[6], Boolean.parseBoolean(userDetails[7]),
					userDetails[8],
					Boolean.parseBoolean(userDetails[9]), userDetails[10], safeParseInt(userDetails[11]),
					userDetails[12], userDetails[13]);
			// if username and password are set, then the profile is complete
			if (patient.getFirstName() != null && patient.getLastName() != null && patient.getUsername() != null
					&& patient.getAge() != 0 && patient.getDob() != null && patient.get_password() != null) {
				System.out.println("Profile already completed. Please login to continue.");
				main(args);
			}
			System.out.println(patient.get_uuid());
			System.out.println(patient.get_email());
			completePatientProfile(patient, line_number);
			main(args);
		} else if (choice.equals("3")) {
			System.out.println("Goodbye");
			System.exit(0);
		} else {
			System.out.println("Invalid choice");
			System.exit(0);
		}
	}

	public static boolean login(String username, String password) {
		System.out.println("Please login to continue");

		if (username.equals("admin")) {
			System.out.println("Admin login commenced");
			// Admin login
			if (password.equals("admin")) {
				System.out.println("Admin login successful");
				return true;
			} else {
				System.out.println("Admin login failed");
				return false;
			}
		} else {
			System.out.println("Patient login commenced");
			// Patient login
			try {
				Patient patient = getPatientDetails(username); // the pas
				if (patient != null && patient.get_password().trim().equals(password)) {
					System.out.println("Patient login successful");
					return true;
				} else {
					System.out.println("Patient login failed! Password is incorrect");
					return false;
				}
			} catch (Exception e) {
				System.out.println("Patient login failed! " + e.getMessage());
				return false;
			}
		}
	}

	// Helper method to safely parse integers
	private static int safeParseInt(String value) {
		if (value == null || value.trim().isEmpty()) {
			System.err.println("Error parsing integer: Value is empty or null");
			return 0; // Return a default value or handle as needed
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			System.err.println("Error parsing integer: " + value);
			return 0; // Return a default value or handle as needed
		}
	}

	public static Patient getPatientDetails(String feild) {
		String[] userStore = executeCommand("script/search.sh " + feild + " storage/user-store.txt").split(":");
		System.out.println(Arrays.toString(userStore));
		if (userStore.length < 2) {
			System.out.println("Invalid " + feild + " . Please try again.");
			return null;
		}
		String user = userStore[1];
		String[] userDetails = user.split(",");
		Patient patient = new Patient(userDetails[0], userDetails[1], userDetails[2], safeParseInt(userDetails[3]),
				userDetails[4], userDetails[5], userDetails[6], Boolean.parseBoolean(userDetails[7]), userDetails[8],
				Boolean.parseBoolean(userDetails[9]), userDetails[10], safeParseInt(userDetails[11]),
				userDetails[12], userDetails[13]);
		return patient;
	}

	public static String getPatientCountryISO(String country) {
		country = country.substring(0, 1).toUpperCase() + country.substring(1); // Capitalize the first letter
		String[] countryStore = executeCommand("script/search.sh " + country.trim() + " storage/life-expectancy.csv").split(",");
		if (countryStore.length < 7) {
			System.out.println("Invalid " + country + " . Please try again.");
			return null;
		}
		return countryStore[5];
	}

	public static String hashUserPassword(String password) {
		return executeCommand("script/hashpwd.sh");
	}

	public static String executeCommand(String scriptPath) {
		String output = "";
		try {
			Process process = Runtime.getRuntime().exec(scriptPath);

			// Read output from the script
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				output = line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return output;
	}
}