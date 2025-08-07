#!/bin/bash
# 관리자 웹어플리케이션 구동
kubectl apply -f ./k3s/configmap.yaml -n catascan
kubectl apply -f ./k3s/catascan-api-deployment.yaml -n catascan
kubectl apply -f ./k3s/catascan-api-service.yaml -n catascan
# 그라파나 및 프로메테우스 구동
#kubectl apply -f ./k3s/prometheus-config.yaml -n catascan
#kubectl apply -f ./k3s/grafana-pvc.yaml -n catascan
#kubectl apply -f ./k3s/prometheus-data-pvc.yaml -n catascan
#kubectl apply -f ./k3s/prometheus-deployment.yaml -n catascan
#kubectl apply -f ./k3s/prometheus-service.yaml -n catascan
#kubectl apply -f ./k3s/grafana-deployment.yaml -n catascan
#kubectl apply -f ./k3s/grafana-service.yaml -n catascan
# 인그레스 및 오토스케일링
kubectl apply -f ./k3s/ingress-http.yaml -n catascan
kubectl apply -f ./k3s/autoscale.yaml -n catascan

