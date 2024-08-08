#! /bin/bash

# Export to the destination file
# 1 - source file, 2 - destination file

# Add column titles as the first line
echo "FirstName,LastName,Username,Age,DOB,Email,UUID,HIV_Positive,Diagnosis_Date,On_ART,Medication_Start_Date,Years_Without_Medication,Password,Country_of_Residence" > "$2"

# Skip the first line (admin data) and append the rest of the data
tail -n +2 "$1" >> "$2"
