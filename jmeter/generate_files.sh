#!/usr/bin/env bash
for ((i = 1; i <= $1; i++))
do
  echo "test content for file_$i.txt" > file_$i.txt
done
