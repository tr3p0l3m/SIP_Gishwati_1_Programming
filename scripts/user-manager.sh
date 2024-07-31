# !/bin/bash

# Location of the user store file
USER_STORE="../user-store.txt"

# Hashing function using OpenSSL
function hash_password {
    echo -n "$1" | openssl dgst -sha256 | awk '{print $2}'
}

# Generate a UUID function
function generate_uuid {
    cat /proc/sys/kernel/random/uuid
}

# Register patient by admin
function register_patient {
    local email=$1
    local uuid=$(generate_uuid)

    # Store the user with email and UUID, role as PATIENT
    echo "$email:$uuid::PATIENT" >> $USER_STORE
    echo "Registration initiated for $email with UUID: $uuid"
}

# Complete the patient's registration
function complete_registration {
    local uuid=$1
    local firstName=$2
    local lastName=$3
    local dob=$4
    local hasHIV=$5
    local diagnosisDate=$6
    local onART=$7
    local artStartDate=$8
    local country=$9
    local password=${10}
    local hashed_password=$(hash_password "$password")

    # Update the user-store.txt with the completed profile
    sed -i "s/^.*:$uuid::PATIENT$/$firstName:$lastName:$dob:$hasHIV:$diagnosisDate:$onART:$artStartDate:$country:$hashed_password:PATIENT/" $USER_STORE
    echo "Registration completed for UUID: $uuid"
}

# Admin adds the first admin user
function initialize_admin {
    echo "Please provide the admin email:"
    read adminEmail
    uuid=$(generate_uuid)
    hashed_password=$(hash_password "admin")
    echo "$adminEmail:$uuid:$hashed_password:ADMIN" >> $USER_STORE
    echo "Admin registered with UUID: $uuid"
}

# Check if the user store exists, if not create an initial admin
if [ ! -f $USER_STORE ]; then
    initialize_admin
fi

# Command-line argument handling
case $1 in
    register_patient)
        register_patient "$2"
        ;;
    complete_registration)
        complete_registration "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}"
        ;;
    *)
        echo "Usage: $0 {register_patient <email> | complete_registration <uuid> <firstName> <lastName> <dob> <hasHIV> <diagnosisDate> <onART> <artStartDate> <country> <password>}"
        ;;
esac

