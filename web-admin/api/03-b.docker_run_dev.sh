#!/bin/bash
docker run -d --name=catascan-web -e SPRING_PROFILES_ACTIVE="dev" \
	-e SPRING_DATASOURCE_URL="jdbc:postgresql://catascan-db-instance-1.clh5zoristtn.ap-northeast-2.rds.amazonaws.com:5432/catascan?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false" \
	-e SPRING_DATASOURCE_USERNAME="postgres" \
	-e SPRING_DATASOURCE_PASSWORD="eP=:rr7n+FqmQd5" \
	-e APP_HOST_URL="http://ec2-43-203-128-20.ap-northeast-2.compute.amazonaws.com" \
	-e APP_HOST_PORT="8080" \
	-v web_eye-images:/app/uploads \
	-v web_admin-logs:/app/logs \
	-p 8080:8080 \
	--restart=always \
	--cpus='0.6' \
	--net=web_eyelab-net catascan-api:1.0.18

