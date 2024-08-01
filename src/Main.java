import controllers.AdminController;
import controllers.PatientController;
import models.Patient;

public class Main {

    public static void main(String[] args) {
        AdminController adminController = new AdminController();
        adminController.registerPatient("patient@example.com");

        PatientController patientController = new PatientController();
        Patient patient = new Patient("patient@example.com", "hashedPassword", "1990-01-01", true, "2022-05-01", true, "2022-06-01", "USA");
        patientController.completeRegistration(patient);
    }
}
