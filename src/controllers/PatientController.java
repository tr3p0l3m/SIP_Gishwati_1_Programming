package controllers;

import models.Patient;

public class PatientController {

    public void completeRegistration(Patient patient) {
        String command = String.format("../scripts/user-manager.sh complete_registration %s %s %s %s %b %s %b %s %s %s",
                patient.getEmail(), patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth(),
                patient.isHasHIV(), patient.getDiagnosisDate(), patient.isOnART(), patient.getArtStartDate(),
                patient.getCountry(), patient.getPassword());

        String output = UserManager.executeBashCommand(command);
        System.out.println(output);
    }

    // Other Patient-specific operations
}
