#!bin/bash

# Delete the specific line in the file
sed -i "${line_number}d" "$user-store.txt"
echo "Line $line_number in $user-store.txt has been deleted."