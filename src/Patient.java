import utilis.UserRole;

public class Patient extends User {
    private String dateOfBirth;
    private boolean hasHIV;
    private String diagnosisDate;
    private boolean onART;
    private String artStartDate;
    private String countryOfResidence;

    public Patient(String email, String uuid) {
        super("", "", email, "");
        this.uuid = uuid;
    }

    public void completeRegistration(String firstName, String lastName, String dateOfBirth,
                                     boolean hasHIV, String diagnosisDate, boolean onART,
                                     String artStartDate, String countryOfResidence, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.hasHIV = hasHIV;
        this.diagnosisDate = diagnosisDate;
        this.onART = onART;
        this.artStartDate = artStartDate;
        this.countryOfResidence = countryOfResidence;
        this.password = password;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PATIENT;
    }
}
