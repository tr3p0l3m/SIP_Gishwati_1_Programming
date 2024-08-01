package Controller;

import java.io.*;

import Controller.Database.DatabaseController;
import Model.*;

public class MainController {

	// function that checks if storage.txt exists
	public static void checkStorage() {

		File file = new File("user-store.txt");
		if (!file.exists()) {
			System.out.println("Welcome to Life Prognosis App! Initializing application...");
			runCommand("echo 'admin,admin' > user-store.txt");
			System.out.println("Initialization complete");
		} else {
			System.out.println("Welcome to Life Prognosis App!");
		}
	}

	// function that takes user input
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

	// function that calculates life expectancy given lifespan, age and years
	// without medication*90%
	public static double lifeExpectancy(int lifespan, int age, int yearsWithoutMedication) {
		double lifeExpectancy = Math.ceil(((lifespan - age) * Math.pow(0.9, yearsWithoutMedication)));
		return lifeExpectancy;
	}

	public static String generateUUID() {
		String prefix = "LPT";
		DatabaseController cmd = new DatabaseController();
		String uuid = runCommand(cmd.getLineCount("user-store.txt"));
		//ensure the the uuid is made up of 4 digits
		if (uuid.length() < 5) {
			int zeros = 5 - uuid.length();
			for (int i = 0; i < zeros; i++) {
				uuid = "0" + uuid;
			}
		}
		System.out.println("UUID: " + prefix + uuid);
		return prefix + uuid;
	}

	// function for login
	public static boolean login(String username, String password) {
		System.out.println("Please login to continue");
		String[] userStore = runCommand("cat user-store.txt").split("\n");
		for (String user : userStore) {
			String[] userCredentials = user.split(",");
			System.out.println("User: " + userCredentials[0] + " Password: " + userCredentials[1]);
			if (userCredentials[0].equals(username) && userCredentials[1].equals(password)) {
				System.out.println("Login successful");
				return true;
			}
		}
		System.out.println("Login failed");
		return false;
	}

	// function that creates a new patient profile using the Patient class
	public static Patient createPatientProfile() {
		Patient patient = new Patient(null, null, null, 0, null, null, null, false, null, false, null, 0);
		patient.set_email(userInput("Enter email: "));
		patient.set_uuid(generateUUID());
		return patient;
	}
	
	// function that creates a new patient profile using the Patient class
	public static Patient completePatientProfile() {
		String first_name = userInput("Enter first name: ");
		String last_name = userInput("Enter last name: ");
		String username = userInput("Enter username: ");
		int age = Integer.parseInt(userInput("Enter age: "));
		String dob = userInput("Enter date of birth: ");
		String email = userInput("Enter email: ");
		String uuid = userInput("Enter uuid: ");
		boolean is_hiv_positive = Boolean.parseBoolean(userInput("Is patient HIV positive? "));
		String diagnosis_date = userInput("Enter diagnosis date: ");
		boolean is_on_antiretroviral_therapy = Boolean
				.parseBoolean(userInput("Is patient on antiretroviral therapy? "));
		String medication_start_date = userInput("Enter medication start date: ");
		int years_without_medication = Integer.parseInt(userInput("Enter years without medication: "));
		Patient patient = new Patient(first_name, last_name, username, age, dob, email, uuid, is_hiv_positive,
				diagnosis_date,
				is_on_antiretroviral_therapy, medication_start_date, years_without_medication);
		return patient;
	}

	public static void main(String[] args) {
		checkStorage();
		// Ask user to Login or Complete Profile
		String choice = userInput("Please choose:  \n1.Login \n2.Complete Profile \n3.Quit: ");
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
				// ask admin to create a new patient profile, update patient profile or export patient data
				String adminChoice = userInput(
						"Please choose:  \n1.Create new patient profile \n2.Update patient profile \n3.Export patient data \n4.Export patient : ");
				if (adminChoice.equals("1")) {
					Patient patient = createPatientProfile();
					System.out.println("Patient profile created successfully");
					System.out.println(patient);
					main(args);
				} else if (adminChoice.equals("2")) {
					Patient updatedPatient = new Patient(null, null, null, 0, null, null, null, false, null, false,
							null, 0);
					String patient_uuid = userInput("Enter patient uuid: ");
					System.out.println("Getting" + patient_uuid + "...");
					updatedPatient.set_uuid(generateUUID());
					updatedPatient.toString();
					System.out.println("Patient profile updated successfully");
					System.out.println(updatedPatient);
					main(args);
				} else if (adminChoice.equals("3")) {
					// export patient data
					runCommand("cat user-store.txt > patient-data.csv");
					System.out.println("Patient data exported successfully");
					main(args);
				} else if (adminChoice.equals("4")) {
					// export patient data
					runCommand("cat user-store.txt > patient-analytics.csv");
					System.out.println("Patient data exported successfully");
					main(args);
				} else {
					System.out.println("Invalid choice");
					main(args);
				}
			} else {
				System.out.println("Welcome Patient " + username);
				Patient patient = completePatientProfile();
				//Ask patient to view their profile, update their profile or delete their profile
				String patientChoice = userInput(
						"Please choose:  \n1.View patient profile \n2.Update patient profile \n3.Delete patient profile: ");
				if (patientChoice.equals("1")) {
					System.out.println(patient);
				} else if (patientChoice.equals("2")) {
					Patient updatedPatient = new Patient(null, null, null, 0, null, null, null, false, null, false,
							null, 0);
					String patient_uuid = userInput("Enter patient uuid: ");
					updatedPatient.set_uuid(patient_uuid);
					updatedPatient.toString();
					System.out.println("Patient profile updated successfully");
					System.out.println(updatedPatient);
				} else if (patientChoice.equals("3")) {
					System.out.println("Patient profile deleted successfully");
					main(args);
				} else {
					System.out.println("Invalid choice");
				}
			}
		} else if (choice.equals("2")) {
			Patient patient = completePatientProfile();
			System.out.println("Patient profile created successfully");
			System.out.println(patient);
			main(args);
		} else if (choice.equals("3")) {
			System.out.println("Goodbye");
			System.exit(0);
		} else {
			System.out.println("Invalid choice");
			System.exit(0);
		}


		// String username = userInput("Enter username: ");
		// String password = userInput("Enter password: ");
		//// login and continue if successful but retry if failed
		// while (!login(username, password)) {
		// 	username = userInput("Enter username: ");
		// 	password = userInput("Enter password: ");
		// }

		// Patient patient = createPatientProfile();
		// System.out.println("Patient profile created successfully");
		// System.out.println(patient);
	}
}