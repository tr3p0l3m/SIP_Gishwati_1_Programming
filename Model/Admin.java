package Model;

public class Admin extends User {
	String password;

	public Admin(String first_name, String last_name, String username, int age, String dob, String password) {
		super(first_name, last_name, username, age, dob);
		this.password = password;
	}

	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}