package Controller.Database;

public class DatabaseController {
	public String getLineCount(String filename) {
		return "sed -n '$=' " + filename;
	}
}
