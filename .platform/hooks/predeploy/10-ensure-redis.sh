#!/bin/bash

if ! command -v redis-server &> /dev/null
then
    echo "Redis could not be found, installing..."
    sudo yum install -y redis
fi

sudo sed -i 's/bind 127.0.0.1/# bind 127.0.0.1/g' /etc/redis.conf
sudo sed -i 's/protected-mode yes/protected-mode no/g' /etc/redis.conf

sudo service redis start

if ps aux | grep redis-server | grep -v grep > /dev/null
then
    echo "Redis started successfully."
else
    echo "Failed to start Redis."
    exit 1
fi
