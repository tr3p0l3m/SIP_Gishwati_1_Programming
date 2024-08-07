package Model;

public class Admin extends User {
	String password;

	public Admin(String first_name, String last_name, String username, int age, String dob, String password) {
		super(first_name, last_name, username, age, dob, role.ADMIN);
		this.password = password;
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public String getUsername() {
		return username;
	}

	public int getAge() {
		return age;
	}

	public String getDOB() {
		return dob;
	}

	public String getPassword() {
		return password;
	}

	public role getRole() {
		return role.ADMIN;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setDOB(String dob) {
		this.dob = dob;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}