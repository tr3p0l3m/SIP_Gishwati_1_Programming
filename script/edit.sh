#!bin/bash

#Edit the specific line in the file

sed -i "${line_number}s/.*/${new_content}/"
"$user-store.txt"

echo "Line $line_number in $user-store.txt has been updated to: $new_content"