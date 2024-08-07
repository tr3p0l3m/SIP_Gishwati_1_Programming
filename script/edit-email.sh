#! /bin/bash

echo "Editing email ${1} to ${2}"
sed -i "s/${1}/${2}/" storage/user-store.txt