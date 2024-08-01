package Model;

public class User {
	String first_name;
	String last_name;
	String username;
	int age;
	//dob
	String dob;

	public User() {
	}

	public User(String first_name, String last_name, String username, int age, String dob) {
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
}
