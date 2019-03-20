#!/bin/bash
myFile="~/springbootdemo-0.0.1-SNAPSHOT.jar"
if [ -f "$myFile" ]
then
sudo rm -rf /springbootdemo-0.0.1-SNAPSHOT.jar 
sudo rm -rf ~/springbootdemo-0.0.1-SNAPSHOT.jar 
pid=$(ps aux | grep "java -jar" | grep "root" | awk '{print $2}')
sudo kill -9 "$pid" 
fi