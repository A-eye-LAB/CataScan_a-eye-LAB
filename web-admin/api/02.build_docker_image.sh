#!/bin/bash
docker run -d --name local-registry \
  -p 5000:5000 \
  --restart=always \
  registry:2

sudo systemctl restart docker

docker build -t catascan-api:1.0.0 .
docker tag catascan-api:1.0.0 localhost:5000/catascan-api:1.0.0
docker push localhost:5000/catascan-api:1.0.0
