#!/bin/bash

sudo cp /home/centos/vars.sh .
sudo cp /home/centos/springbootdemo-0.0.1-SNAPSHOT.jar .
sudo chmod +x ./vars.sh
sudo ./vars.sh >/dev/null 2>&1 &
#sudo vars.sh >/dev/null 2>&1 &
