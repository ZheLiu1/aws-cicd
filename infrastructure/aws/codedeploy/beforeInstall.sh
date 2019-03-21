#!/bin/bash
cd ~
if [ -f "springbootdemo-0.0.1-SNAPSHOT.jar" ]
then
sudo rm -rf /springbootdemo-0.0.1-SNAPSHOT.jar
sudo rm -rf /home/centos/springbootdemo-0.0.1-SNAPSHOT.jar 
sudo rm -rf /home/centos/cloudwatch-config.json
sudo rm -rf /home/centos/csye6225.log
pid=$(ps aux | grep "java -jar" | grep "root" | awk '{print $2}')
sudo kill -9 "$pid" 
sudo systemctl stop amazon-cloudwatch-agent.service
fi