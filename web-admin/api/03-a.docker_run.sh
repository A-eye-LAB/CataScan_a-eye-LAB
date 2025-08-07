#!/bin/bash
docker run --name=catascan-web -e SPRING_PROFILES_ACTIVE="prod" \
	-e SPRING_DATASOURCE_URL="jdbc:postgresql://catascan-db.cluster-c0g3rjffdijh0.ap-northeast-2.rds.amazonaws.com:5432/postgres?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false" \
	-e SPRING_DATASOURCE_USERNAME="postgres" \
	-e SPRING_DATASOURCE_PASSWORD="catascan1!" \
	-e APP_IMAGE_BUCKET_NAME="eyelab-test" \
	-e APP_IMAGE_BUCKET_REGION="ap-northeast-2" \
	-e AWS_ACCESS_KEY_ID="IUERAB9GERGNPDOFV" \
	-e AWS_SECRET_ACCESS_KEY="DXT9G3N0WNEG0GBDA9Gibors0gejndXjaHA" \
	-p 8080:8080 \
	-d catascan-api:1.0.0
