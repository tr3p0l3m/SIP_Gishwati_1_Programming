#! /bin/bash

# Compare this snippet from script/edit-email.sh:

echo -n $1 | openssl dgst -sha256 -binary | base64