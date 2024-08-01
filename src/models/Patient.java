package models;

public class Patient extends User {
    private String dateOfBirth;
    private boolean hasHIV;
    private String diagnosisDate;
    private boolean onART;
    private String artStartDate;
    private String country;

    public Patient(String email, String password, String dateOfBirth, boolean hasHIV, String diagnosisDate, boolean onART, String artStartDate, String country) {
        super(email, password, UserRole.PATIENT);
        this.dateOfBirth = dateOfBirth;
        this.hasHIV = hasHIV;
        this.diagnosisDate = diagnosisDate;
        this.onART = onART;
        this.artStartDate = artStartDate;
        this.country = country;
    }

    // Getters and Setters
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isHasHIV() {
        return hasHIV;
    }

    public String getDiagnosisDate() {
        return diagnosisDate;
    }

    public boolean isOnART() {
        return onART;
    }

    public String getArtStartDate() {
        return artStartDate;
    }

    public String getCountry() {
        return country;
    }
}
