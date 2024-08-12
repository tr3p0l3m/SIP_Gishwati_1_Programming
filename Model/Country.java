package Model;

public class Country {

	String name;
	String code;
	Double life_expectancy;

	public Country(String name, String code, Double life_expectancy) {
		this.name = name;
		this.code = code;
		this.life_expectancy = life_expectancy;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public String get_code() {
		return code;
	}

	public void set_code(String code) {
		this.code = code;
	}

	public Double get_life_expectancy() {
		return life_expectancy;
	}

	public void set_life_expectancy(Double life_expectancy) {
		this.life_expectancy = life_expectancy;
	}
	
}
