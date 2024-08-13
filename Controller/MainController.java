package Controller;

import Model.Admin;
import Model.Country;
import Model.Patient;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

public class MainController {

	public static void checkStorage() {
		File file = new File("storage/user-store.txt");
		if (!file.exists()) {
			System.out.println("Welcome to Life Prognosis App! Initializing application...");
			String choice = userInput("Would you like to create a custom admin account? (Y/N): ", "none", 0, 0, 0);
			Admin admin = null;
			if (choice.equalsIgnoreCase("Y")) {
				String firstName = userInput("Enter your first name: ", "none", 30, 0, 0);
				String lastName = userInput("Enter your last name: ", "none", 30, 0, 0);
				String dob = userInput("Enter date of birth (DD-MM-YYYY): ", "date", 0, 0, 0);
				int age = ageCalculator(dob);
				String password = hashUserPassword(passwordInput(true));
				admin = new Admin(firstName, lastName, "admin", age, dob, password.trim());
			} else {
				String password = hashUserPassword("admin");
				admin = new Admin("admin", "admin", "admin", 0, null, password);
			}
			String adminDetails = admin.getFirstName() + "," + admin.getLastName() + "," + admin.getUsername() + ","
					+ admin.getAge() + "," + admin.getDOB() + "," + admin.getPassword().trim();
			executeCommand(new String[] { "script/insert.sh", adminDetails });
			System.out.println("Initialization complete");
		} else {
			System.out.println("Welcome to Life Prognosis App!");
		}
	}

	public static String userInput(String message, String type, int length, int min, int max) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(message);
		String input = null;
		try {
			input = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (length > 0 && input.length() > length) {
			System.out.println("Invalid length input. Please try again.");
			return userInput(message, type, length, min, max);
		}

		// check if input is within range
		if ((type == "number") && (min > 0 || max > 0) && (safeParseInt(input) < min || safeParseInt(input) > max)) {
			System.out.println("Invalid range input. Please try again.");
			return userInput(message, type, length, min, max);
		}
		// validate input based on type
		switch (type) {
			case "date":
				// validate date
				Date date = null;
				String inputDate = input;
				try {
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					formatter.setLenient(false);
					date = formatter.parse(inputDate);
				} catch (ParseException e) {
					e.printStackTrace();
				} finally {
					if (date == null) {
						System.out.println("Invalid date. Please try again.");
						return userInput(message, type, length, min, max);
					}
				}

				input = new SimpleDateFormat("dd-MM-yyyy").format(date);

				break;
			case "email":
				// validate email
				Pattern emailPattern = Pattern
						.compile("^[a-zA-Z0-9].[a-zA-Z0-9\\._%\\+\\-]{0,63}@[a-zA-Z0-9\\.\\-]+\\.[a-zA-Z]{2,30}$");
				Matcher emailMatcher = emailPattern.matcher(input);
				if (!emailMatcher.matches()) {
					System.out.println("Invalid email. Please try again.");
					return userInput(message, type, length, min, max);
				}
				// check if it already exists
				String[] userStore = executeCommand(
						new String[] { "script/search.sh", input, "storage/user-store.txt" }).split(":");
				if (userStore.length > 1) {
					System.out.println("Email already exists. Please try again.");
					return userInput(message, type, length, min, max);
				}
				break;
			case "username":
				// validate username
				Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9._%+-]{3,}$");
				Matcher usernameMatcher = usernamePattern.matcher(input);
				if (!usernameMatcher.matches()) {
					System.out.println("Invalid username. Please try again.");
					return userInput(message, type, length, min, max);
				}
				// check if it already exists
				String[] userStore2 = executeCommand(
						new String[] { "script/search.sh", input, "storage/user-store.txt" }).split(":");
				if (userStore2.length > 1) {
					System.out.println("Username already exists. Please try again.");
					return userInput(message, type, length, min, max);
				}
				break;

			case "none":
				break;

			default:
				break;
		}
		return input;
	}

	public static double lifeExpectancy(String country, double age, int yearsWithoutMedication) {
		String[] countryStore;
		country = country.trim();
		countryStore = executeCommand(new String[] { "script/search.sh", country, "storage/life-expectancy.csv" })
				.split(",");
		if (countryStore.length < 7) {
			System.out.println("Invalid " + country + " . Please try again.");
			return 0;
		}
		double lifespan = Double.parseDouble(countryStore[6]);
		return Math.ceil(((lifespan - age) * Math.pow(0.9, yearsWithoutMedication)));
	}

	public static Patient initiatePatientProfile() {
		Patient patient = new Patient(null, null, null, 0, null, null, null, false, null, false, null, 0, null, null);
		patient.set_email(userInput("Enter patient's email: ", "email", 0, 0, 0));
		patient.set_uuid(executeCommand(new String[] { "script/uuidgen.sh" }).trim());

		String patientDetails = patient.getFirstName() + "," + patient.getLastName() + "," + patient.getUsername() + ","
				+ patient.getAge() + "," + patient.getDob() + "," + patient.get_email() + "," + patient.get_uuid() + ","
				+ patient.is_hiv_positive() + "," + patient.get_diagnosis_date() + ","
				+ patient.is_on_antiretroviral_therapy() + ","
				+ patient.get_medication_start_date() + "," + patient.get_years_without_medication() + ","
				+ patient.get_password() + "," + patient.get_country_of_residence();

		// Save the new patient profile (email, UUID) to user-store.txt
		executeCommand(new String[] { "script/insert.sh", patientDetails });

		System.out.println("Patient profile initiated. UUID generated: " + patient.get_uuid());
		return patient;
	}

	public static Patient completePatientProfile(Patient patient, int line_number) {
	// Prompt for personal details only
	patient.setFirstName(userInput("Enter first name: ", "none", 30, 0, 0));
	patient.setLastName(userInput("Enter last name: ", "none", 30, 0, 0));
	patient.setUsername(userInput("Enter username: ", "username", 30, 0, 0));
	patient.setDob(userInput("Enter date of birth (DD-MM-YYYY): ", "date", 0, 0, 0));
	patient.setAge(ageCalculator(patient.getDob()));
	Country country = getCountryDetails(userInput("Enter country of residence: ", "none", 0, 0, 0));
	patient.set_country_of_residence(country.get_code());
	patient.set_password(hashUserPassword(passwordInput(true))); // Prompt for the password

	// Tell patient that the next steps are optional and can be completed later. Ask if they want to continue
	String optional = userInput("The next steps are optional and can be completed later. Do you want to continue? (Y/N): ", "none", 0, 0, 0);
	if (optional.equalsIgnoreCase("Y")) {
		// Prompt for medical details with multiple choice
		patient.set_hiv_positive(askYesNoQuestion("Are you HIV positive?"));
		if (patient.is_hiv_positive()) {
			patient.set_diagnosis_date(userInput("Enter diagnosis date (DD-MM-YYYY): ", "date", 0, 0, 0));
			patient.set_on_antiretroviral_therapy(askYesNoQuestion("Are you on antiretroviral therapy?"));
			if (patient.is_on_antiretroviral_therapy()) {
				patient.set_medication_start_date(userInput("Enter medication start date (DD-MM-YYYY): ", "date", 0, 0, 0));
				patient.set_years_without_medication(yearsWithoutMedication(patient.get_diagnosis_date(), patient.get_medication_start_date()));
			} else {
				patient.set_years_without_medication(0);
			}
		}
	}

	// TODO: confirm password

	// Construct the new line with updated details
	String updatedLine = patient.getFirstName() + "," + patient.getLastName() + "," + patient.getUsername() + ","
			+ patient.getAge() + "," + patient.getDob() + "," + patient.get_email() + "," + patient.get_uuid() + ","
			+ patient.is_hiv_positive() + "," + patient.get_diagnosis_date() + ","
			+ patient.is_on_antiretroviral_therapy() + "," + patient.get_medication_start_date() + ","
			+ patient.get_years_without_medication() + "," + patient.get_password() + "," + patient.get_country_of_residence();

	updatedLine = updatedLine.replace("/", "-");

	// Update the user's line in the user-store.txt file
	executeCommand(new String[] { "script/edit.sh", Integer.toString(line_number), updatedLine });

	// Notify the user that the profile has been completed
	System.out.println("Profile completed successfully. Please log in with your credentials.");
	return patient;
}

private static boolean askYesNoQuestion(String question) {
	while (true) {
		String input = userInput(question + "\n1. Yes\n2. No\nChoose an option (1 or 2): ", "none", 0, 0, 0);
		if (input.equals("1")) {
			return true;
		} else if (input.equals("2")) {
			return false;
		} else {
			System.out.println("Invalid input. Please enter 1 for Yes or 2 for No.");
		}
	}
}


	public static void main(String[] args) {
		checkStorage();
		// Ask user to Login or Complete Profile
		String choice = userInput("Please choose: \n1.Login \n2.Complete Profile \n3.Quit: ", "number", 0, 1, 3);
		if (choice.equals("1")) {
			// login and continue if successful but retry if failed
			String username = userInput("Enter username: ", "none", 0, 0, 0);
			String password = passwordInput(false);
			while (!login(username, password)) {
				System.out.println("Login failed. Please try again.");
				username = userInput("Enter username: ", "none", 0, 0, 0);
				password = passwordInput(false);
			}

			if (username.equals("admin")) {
				System.out.println("Welcome Admin");
				// ask admin to create a new patient profile, update patient profile or export
				// patient data
				String adminChoice = userInput(
						"Please choose: \n1.Create new patient profile \n2.Update patient profile \n3.Delete Patient Profile \n4.Export patient data \n5.Export patient analytics \n6.Edit Admin Details \n7.Logout ", "number", 0, 1, 7);
				if (adminChoice.equals("1")) {
					initiatePatientProfile();
					System.out.println("Patient profile created successfully");
					main(args);
				} else if (adminChoice.equals("2")) {
					String patient_uuid = userInput("Enter patient uuid: ", "none", 0, 0, 0);
					Patient patient = getPatientDetails(patient_uuid);
					// update patient email
					String updatedEmail = userInput("Enter new email: ", "email", 0, 0, 0).trim();
					// replace the email in the user-store.txt with the updated email
					executeCommand(new String[] { "script/edit-email.sh", patient.get_email(), updatedEmail });
					System.out.println("Patient profile updated successfully");
					main(args);
				} else if (adminChoice.equals("3")) {
					String uuid = userInput("Enter UUID: ", "none", 0, 0, 0);
					String line_number = executeCommand(
							new String[] { "script/search.sh", uuid, "storage/user-store.txt" }).split(":")[0];
					executeCommand(new String[] { "script/delete.sh", line_number });
					System.out.println("Patient profile deleted successfully!");
					main(args);
				} else if (adminChoice.equals("4")) {
					// export patient data
					// TODO: remember to format the export
					executeCommand(
							new String[] { "script/export.sh", "storage/user-store.txt", "storage/patient-data.csv" });
					System.out.println("Patient data exported successfully to storage/patient-data.csv");
					main(args);
				} else if (adminChoice.equals("5")) {
					// export patient data
					executeCommand(new String[] { "touch", "storage/patient-analytics.csv" });
					System.out.println("Patient data exported successfully to storage/patient-analytics.csv");
					main(args);
				} else if (adminChoice.equals("6")) {
					// edit admin details
					String line_number = executeCommand(
							new String[] { "script/search.sh", "admin", "storage/user-store.txt" })
							.split(":")[0];
					Admin admin = getAdminDetails("admin");
					admin.setFirstName(userInput("Enter first name: ", "none", 30, 0, 0));
					admin.setLastName(userInput("Enter last name: ", "none", 30, 0, 0));
					admin.setDOB(userInput("Enter date of birth (DD-MM-YYYY): ", "date", 0, 0, 0));
					admin.setAge(ageCalculator(admin.getDOB()));
					admin.setPassword(hashUserPassword(passwordInput(true)));
					System.out.println(admin.getPassword());
					String updatedLine = admin.getFirstName() + "," + admin.getLastName() + "," + admin.getUsername()
							+ "," + admin.getAge() + "," + admin.getDOB() + "," + admin.getPassword().trim();
					executeCommand(new String[] { "script/edit.sh", line_number, updatedLine });
					System.out.println("Admin details updated successfully");
					main(args);
				} else if (adminChoice.equals("7")) {
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
						"Please choose: \n1.View patient profile \n2.Update patient profile \n3.Delete patient profile \n4.Logout ", "number", 0, 1, 4);
				if (patientChoice.equals("1")) {
					double lifeExpectancy = lifeExpectancy(patient.get_country_of_residence(),
							(double) patient.getAge(), patient.get_years_without_medication());
					// output patient profile with nice formatting
					System.out.println(patient
							+ "\nLife Expectancy: " + lifeExpectancy);

					userInput("Press Enter to logout", "none", 0, 0, 0);
					main(args);
				} else if (patientChoice.equals("2")) {
					String uuid = patient.get_uuid();
					String[] commands = { "script/search.sh", uuid, "storage/user-store.txt" };
					String[] userStore = executeCommand(commands).split(":");
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
				} else if (patientChoice.equals("4")) {
					System.out.println("Patient logged out successfully");
					main(args);
				} else {
					System.out.println("Invalid choice");
					main(args);
				}
			}
		} else if (choice.equals("2")) {
			String uuid = userInput("Enter UUID: ", "none", 0, 0, 0);
			String[] commands = { "script/search.sh", uuid, "storage/user-store.txt" };
			String[] userStore = executeCommand(commands).split(":");
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
			System.out.println("Editing patient profile for: " + patient.get_email());
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
			Admin admin = getAdminDetails(username);
			System.out.println(admin.getPassword().trim());
			// Admin login
			if (admin.getPassword().trim().equals(hashUserPassword(password).trim())) {
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
				Patient patient = getPatientDetails(username);
				if (patient != null && patient.get_password().trim().equals(hashUserPassword(password).trim())) {
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
		String[] commands = { "script/search.sh", feild, "storage/user-store.txt" };
		String[] userStore = executeCommand(commands).split(":");
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

	public static Admin getAdminDetails(String feild) {
		String[] commands = { "script/search.sh", feild, "storage/user-store.txt" };
		String[] userStore = executeCommand(commands).split(":");
		if (userStore.length < 2) {
			System.out.println("Invalid " + feild + " . Please try again.");
			return null;
		}
		String user = userStore[1];
		String[] userDetails = user.split(",");
		Admin admin = new Admin(userDetails[0], userDetails[1], userDetails[2], safeParseInt(userDetails[3]),
				userDetails[4], userDetails[5]);
		return admin;
	}

	public static Country getCountryDetails(String country) {
		country = country.substring(0, 1).toUpperCase() + country.substring(1); // Capitalize the first letter
		String[] commands = { "script/search.sh", country.trim(), "storage/life-expectancy.csv" };
		String[] countryStore = executeCommand(commands).split(":");
		if (countryStore.length < 2) {
			System.out.println("Invalid " + country + " . Please try again.");
		}
		String[] countryDetails = countryStore[1].split(",");
		return new Country(countryDetails[0], countryDetails[5], Double.parseDouble(countryDetails[6]));
	}

	public static String hashUserPassword(String password) {
		String[] commands = { "script/hashpwd.sh", password };
		return executeCommand(commands).trim();
	}

	public static String executeCommand(String[] commands) {

		String output = "";
		try {
			Process process = Runtime.getRuntime().exec(commands);

			// Read output from the script
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// process wait for 30 milliseconds
			process.waitFor(30, java.util.concurrent.TimeUnit.MILLISECONDS);

			String line = "";
			while ((line = reader.readLine()) != null) {
				output = line + "\n";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		return output;
	}

	public static String passwordInput(boolean confirm) {
		Console console = System.console();
		if (console == null) {
			System.out.println("Couldn't get Console instance");
			System.exit(0);
		}

		char[] passwordArray = null;
		String password = null;

		while (confirm) {
			// confirm password
			passwordArray = console.readPassword("Enter your secret password: ");
			char[] confirmPasswordArray = console.readPassword("Confirm your secret password: ");

			if (passwordArray.length != confirmPasswordArray.length) {
				console.printf("Passwords do not match%n");
				continue;
			}

			password = new String(passwordArray);
			String confirmPassword = new String(confirmPasswordArray);

			if (password.equals(confirmPassword)) {
				console.printf("Passwords match%n");
				return password;
			} else {
				console.printf("Passwords do not match%n");
			}
		}

		passwordArray = console.readPassword("Enter your secret password: ");
		password = new String(passwordArray);

		return password;
	}

	public static int ageCalculator(String dob) {
		// calculate age
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		try {
			date = formatter.parse(dob);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date currentDate = new Date();
		long diff = currentDate.getTime() - date.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		long diffYears = diffDays / 365;
		System.out.println("Age: " + diffYears);
		return (int) diffYears;
	}

	public static int yearsWithoutMedication(String diagnosisDate, String medicationStartDate) {
		// get difference between medication start date and diagnosis date
		int yearsWithoutMedication = safeParseInt(medicationStartDate.substring(6, 10))
				- safeParseInt(diagnosisDate.substring(6, 10));
		return yearsWithoutMedication;
	}
}
