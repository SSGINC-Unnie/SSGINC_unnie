#!/bin/bash

echo "Installing Redis..."
sudo dnf install redis -y

echo "Configuring Redis protected-mode..."
sudo sed -i 's/protected-mode yes/protected-mode no/g' /etc/redis/redis.conf

echo "Configuring Redis bind address..."
sudo sed -i '/^bind 127.0.0.1/s/^/#/' /etc/redis/redis.conf

echo "Enabling Redis service..."
sudo systemctl enable redis

echo "Starting Redis service..."
sudo systemctl start redis

echo "Redis installation and configuration finished."