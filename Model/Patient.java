package Model;

//TODO: add patient password attribute

public class Patient extends User {
	String email;
	String uuid;
	boolean is_hiv_positive;
	String diagnosis_date;
	boolean is_on_antiretroviral_therapy;
	String medication_start_date;
	int years_without_medication;
	String password;
	String country_of_residence;

	public Patient(String first_name, String last_name, String username,
			int age, String dob, String email, String uuid, boolean is_hiv_positive, String diagnosis_date,
			boolean is_on_antiretroviral_therapy, String medication_start_date, int years_without_medication, String password, String country_of_residence) {
		super(first_name, last_name, username, age, dob, role.PATIENT);
		this.email = email;
		this.uuid = uuid;
		this.is_hiv_positive = is_hiv_positive;
		this.diagnosis_date = diagnosis_date;
		this.is_on_antiretroviral_therapy = is_on_antiretroviral_therapy;
		this.medication_start_date = medication_start_date;
		this.years_without_medication = years_without_medication;
		this.password = password;
		this.country_of_residence = country_of_residence;
	}

	public String get_email() {
		return email;
	}

	public void set_email(String email) {
		this.email = email;
	}

	public String get_uuid() {
		return uuid;
	}

	public void set_uuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean is_hiv_positive() {
		return is_hiv_positive;
	}

	public void set_hiv_positive(boolean is_hiv_positive) {
		this.is_hiv_positive = is_hiv_positive;
	}

	public String get_diagnosis_date() {
		return diagnosis_date;
	}

	public void set_diagnosis_date(String diagnosis_date) {
		this.diagnosis_date = diagnosis_date;
	}

	public boolean is_on_antiretroviral_therapy() {
		return is_on_antiretroviral_therapy;
	}

	public void set_on_antiretroviral_therapy(boolean is_on_antiretroviral_therapy) {
		this.is_on_antiretroviral_therapy = is_on_antiretroviral_therapy;
	}

	public String get_medication_start_date() {
		return medication_start_date;
	}

	public void set_medication_start_date(String medication_start_date) {
		this.medication_start_date = medication_start_date;
	}

	public int get_years_without_medication() {
		return years_without_medication;
	}

	public void set_years_without_medication(int years_without_medication) {
		this.years_without_medication = years_without_medication;
	}

	public String get_password() {
		return password;
	}

	public void set_password(String password) {
		this.password = password;
	}

	public String get_country_of_residence() {
		return country_of_residence;
	}

	public void set_country_of_residence(String country_of_residence) {
		this.country_of_residence = country_of_residence;
	}

	@Override
	public String toString() {
		return this.first_name + "'s Profile \n" + "First Name: "
				+ first_name
				+ "\nLast Name: " + this.last_name + "\nUsername: " + this.username
				+ "\nAge: " + this.age + "\nDate of Birth: " + this.dob + "\nEmail: "
				+ this.email + "\nUUID: " + this.uuid + "\nHIV Positive: "
				+ this.is_hiv_positive + "\nDiagnosis Date: " + this.diagnosis_date
				+ "\nOn Antiretroviral Therapy: " + this.is_on_antiretroviral_therapy
				+ "\nMedication Start Date: " + this.medication_start_date
				+ "\nYears Without Medication: " + this.years_without_medication;
	}

}
