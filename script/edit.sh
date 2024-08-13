#! /bin/bash

#Edit the specific line in the file
#1 - line number, 2 - data
sed "${1}s|.*|${2}|" storage/user-store.txt > temp.txt && mv temp.txt storage/user-store.txt