package Model;

public class User {
	String first_name;
	String last_name;
	String username;
	int age;
	String dob;
	enum role {
		ADMIN, PATIENT
	}

	public User() {
	}

	public User(String first_name, String last_name, String username, int age, String dob, role role) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.age = age;
		this.dob = dob;
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

	public String getDob() {
		return dob;
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

	public void setDob(String dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "First Name: " + first_name + "\n Last Name: " + last_name + "\n Username: " + username + "\n Age: " + age
				+ "\n Date of Birth: " + dob;
	}
}
