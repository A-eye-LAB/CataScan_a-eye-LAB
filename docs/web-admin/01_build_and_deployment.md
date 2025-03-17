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

| 키                                                   | 설명                            | 예시                    |
| :----------------------------------------------------: | :-------------------------------: | :-----------------------: |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`| DB엔드포인트, DB유저명, DB유저암호 | `jdbc:postgresql://localhost:5432/postgres?allowPublicKeyRetrieval=true&useSSL=false`, `postgres`, `dbpassword` |
| `APP_TOKEN_SECRET`, `APP_TOKEN_EXPIRE_HOURS` | 어플리케이션 토큰 암호화키(32글자, 프론트엔드의 `PASETO_KEY`와 동일)와 토큰 유효시간  | `iZeujFSbD8gii21pfx8XFMH56V71inkP`, `12` |
| `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`, `APP_ADMIN_EMAIL`, `APP_ADMIN_INSTITUTION` | 관리자 유저명, 암호(8-32글자), 이메일, 기관명  | `admin`, `ot3Jr6zh58Ft0U4L`, `example@example.com`, `Eyelab` |
| `APP_HOST_URL`, `APP_HOST_PORT` | 웹 어플리케이션 호스트 URL 및 포트(이미지 저장 및 접근 용도, 개발/테스트서버용)  | `http://localhost`, `8080` |
| `APP_PATIENT_RETENTION_DAYS`, `APP_PATIENT_RETENTION_CRON` | 환자 데이터 저장 정책(삭제된 환자 자동 삭제 스케쥴러)  | `2000`, `"0 0 1 * * ?"` |
| `APP_IMAGE_BUCKET_NAME`, `APP_IMAGE_BUCKET_REGION` | S3버킷명 및 리전(운영서버용)  | `catascan-api-bucket`, `ap-northeast-2` |

### How to deploy
- EC2인스턴스, S3버킷, RDS인스턴스, AWS 사용자의 액세스키를 생성하고, EC2인스턴스에 SSH로 접근한 부분에서 시작합니다. 
- (추가 테스트 후 확인된 내용으로 작성할 예정입니다.)



[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 소개](00_introduction.md) |
[다음: 웹 구조](02_web_architecture.md)