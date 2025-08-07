# 빌드 및 배포

이 문서에서는 CataScan 웹 어드민 프론트엔드의 빌드 및 배포 방법에 대해 설명합니다.

## Front-End

### Running Locally

- 로컬에서 정상적으로 실행하기 위해선 [.env.example](./assets/.env.example)과 같은 형태의 파일이 필요합니다. Vercel을 이용하여 환경변수를 관리하길 추천합니다.
- 자세한 내용은 [링크](https://vercel.com/docs/concepts/projects/environment-variables#development-environment-variables)를 참고하세요.

1. Vercel CLI를 설치합니다: `npm i -g vercel`
2. Github 계정 등을 이용해 Vercel 서비스와 연결합니다 : `vercel link`
3. Vercel에 등록된 환경변수를 다운로드합니다: `vercel env pull`

```
npm install
npm run dev // 기본 주소는 localhost:3000 입니다.
```

### 주요 환경 변수

| 키                                                   | 설명                            | 예시                    |
| ---------------------------------------------------- | ------------------------------- | ----------------------- |
| `NEXTAUTH_URL`                                       | 서비스 URL                      | `http://localhost:3000` |
| `NEXTAUTH_SECRET`                                    | 랜덤 비밀 키                    | 랜덤 생성기 발급        |
| `NEXT_PUBLIC_API_URL`                                | API 서버 엔드포인트             | `http://localhost:3000` |
| `NEXT_PUBLIC_HOSTNAME`, `NEXT_PUBLIC_IMAGE_HOSTNAME` | API 서버, 이미지 서버 호스트명  | `http://localhost:3000` |
| `PASETO_KEY`                                         | Login에 사용되는 paseto 비밀 키 | `SECRET_PASETO_KEY`     |

### How to deploy

- Github에 프로젝트를 생성하고, Vercel에 연결하세요.
  - 연결된 Github Repository에서 push 동작이 일어날 때마다, 고유한 링크가 생성됩니다.
- 자세한 내용은 [Vercel 공식 문서](https://vercel.com/docs/deployments)를 참고하세요.


## Back-End

- API를 구성하기 위해서는 다음과 같은 패키지를 미리 설치해야합니다. 
  - Docker
  - Kubernetes
  - Docker Compose 
  - k3s

### Running Locally
- 도커 컴포즈를 통해 catascan-api서버, postgres, prometheus, grafana 컨테이너 구동
```
docker-compose up --build -d
```

### 주요 환경 변수

| 키                                                   |                           설명                           |                                                       예시                                                        |
| :----------------------------------------------------: |:------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------:|
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`|                 DB엔드포인트, DB유저명, DB유저암호                 | `jdbc:postgresql://localhost:5432/postgres?allowPublicKeyRetrieval=true&useSSL=false`, `postgres`, `dbpassword` |
| `APP_TOKEN_SECRET`, `APP_TOKEN_EXPIRE_HOURS` | 어플리케이션 토큰 암호화키(32글자, 프론트엔드의 `PASETO_KEY`와 동일)와 토큰 유효시간 |                                    `iZeujFSbD8gii21pfx8XFMH56V71inkP`, `12`                                     |
| `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`, `APP_ADMIN_EMAIL`, `APP_ADMIN_INSTITUTION` |             관리자 유저명, 암호(8-32글자), 이메일, 기관명              |                         `LabSDAdmin`, `labSDUser2025!`, `example@example.com`, `Eyelab`                         |
| `APP_HOST_URL`, `APP_HOST_PORT` |    웹 어플리케이션 호스트 URL 및 포트(이미지 저장 및 접근 용도, 개발/테스트서버용)    |                                           `http://localhost`, `8080`                                            |
| `APP_PATIENT_RETENTION_DAYS`, `APP_PATIENT_RETENTION_CRON` |            환자 데이터 저장 정책(삭제된 환자 자동 삭제 스케쥴러)             |                                             `2000`, `"0 0 1 * * ?"`                                             |
| `APP_IMAGE_BUCKET_NAME`, `APP_IMAGE_BUCKET_REGION` |                   S3버킷명 및 리전(운영서버용)                    |                                     `catascan-api-bucket`, `ap-northeast-2`                                     |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` |        IAM 등록으로 부여받은 액세스 키ID 및 암호 액세스 키(운영서버용)         |                         `AKIAGIEBARIGNOPVON`, `B3d4*1NA420WJ0JFSD0FJO%!f#!^#H0%o0N!R0bOH`                          |

### How to deploy
- EC2인스턴스, S3버킷, RDS인스턴스, AWS 사용자의 액세스키를 생성하고, EC2인스턴스에 SSH로 접근한 부분에서 시작합니다.

#### 기본 패키지 설치 및 ec2-user 권한 확인
```
# Docker 설치 및 권한 설정
sh 01.install_docker.sh
logout
```
```
# ssh 로그아웃 이후 다시 로그인 후
# Docker 이미지 빌드 후 로컬 레지스트리에 Docker 이미지 업로드
sh 02.build_docker_image.sh
```
```
# Postgres 설치(DB 직접 접근할 수 있도록 설치)
03.install_postgres.sh
```
```
# Kubernetes k3s 설치 및 네임스페이스 catascan 구동
sh 04.install_k3s.sh
```

```
# 각종 암호 설정
# DB 사용자 정보, AWS 사용자 엑세스 키, 앱 토큰 암호키(32글자) 설정 예시
kubectl create secret generic app-secrets \
  --from-literal=db-username='postgres' \
  --from-literal=db-password='secretPassword' \
  --from-literal=aws-access-key='AKIAGOJANGIONARGOPVON' \
  --from-literal=aws-secret-key='B3d4*1NA3RiWXBR@2VgzO%!f#!^#H0%o0N!R0bOH' \
  --from-literal=app-token-secret='n0C029EbHL#6X^b^6z3U8^4$o2#J9h2M' \
  --namespace catascan
```
```
# 그라파나 관련 암호설정 예시
kubectl create secret generic grafana-secrets \
  --from-literal=admin-password='password1' \
  --namespace catascan
```
#### 쿠버네티스를 통한 API 서버 구동. 
```
# k3s 내에 있는 각종 파일을 통한 쿠버네티스 구동( 구동 전 yaml 파일 확인)
# 서버 규모에 따라서 프로메테우스나 그라파나를 운영하는 대신 CloudWatch 활용 모니터링 고려
# 우선 k3s/configmap.yaml, prometheus-config.yaml 확인 후 설정 내용 맞는지 확인
sh 04.kube_deploy.sh
```

#### API 서버 확인
```
curl http://localhost:8080/management/health
```

### * 별도 S3버킷 및 RDS 없이 EC2 인스턴스로 API 서버 구성하기. 
```
# 도커 설치 및 권한 설정
sh 01.install_docker.sh
logout
```
```
# 프로메테우스와 그라파나 없는 도커 컴포즈 run
rm docker-compose.yml Dockerfile
mv docker-compose-light.yml docker-compose.yml
docker-compose up -d
```


### * 별도 모니터링 기능 없이 EC2 인스턴스 내에서 API 서버 구성
```
# 도커 설치 및 권한 설정
sh 01.install_docker.sh
logout
```
```
# 프로메테우스나 그라파나 없는 도커 이미지 빌드
sh 02-a.build_local_docker_image.sh
```
```
# 해당 내용에서 설정변경 후 도커 실행 
# dev프로파일은 로컬파일시스템에 이미지 저장; prod프로파일은 S3버킷에 이미지 저장)
sh 03-a.docker_run.sh
```


[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 소개](00_introduction.md) |
[다음: 웹 구조](02_web_architecture.md)