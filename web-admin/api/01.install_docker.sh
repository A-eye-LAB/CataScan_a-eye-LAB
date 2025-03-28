#!/bin/sh
sudo dnf update
sudo dnf install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user
echo "now logout and login again"
