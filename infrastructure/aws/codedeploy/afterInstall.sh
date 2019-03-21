#!/bin/bash
cd /
sudo cp /home/centos/vars.sh .
sudo cp /home/centos/springbootdemo-0.0.1-SNAPSHOT.jar .
sudo chmod +x ./vars.sh
sudo ./vars.sh >> /home/centos/csye6225.log 2>&1 &
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/home/centos/cloudwatch-config.json \
    -s
