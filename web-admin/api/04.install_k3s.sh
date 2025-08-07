#!/bin/bash
curl -sfL https://get.k3s.io | sh -

sudo ln -s /usr/local/bin/kubectl /usr/bin/kubectl

sudo chmod +rwx /etc/rancher/k3s/k3s.yaml

kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.1/deploy/static/provider/baremetal/deploy.yaml

kubectl create namespace catascan
