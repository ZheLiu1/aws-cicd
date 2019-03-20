#!/bin/bash
cd ~
sudo rm -r springbootdemo-0.0.1-SNAPSHOT.jar >/dev/null 2>&1
pid = $(ps aux | grep "java -jar" | grep "root")
sudo kill -9 $pid >/dev/null 2>&1