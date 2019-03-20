#!/bin/bash
cd ~
sudo rm -r springbootdemo-0.0.1-SNAPSHOT.jar
pid = $(ps aux | grep "java -jar" | grep "root")
sudo kill -9 $pid