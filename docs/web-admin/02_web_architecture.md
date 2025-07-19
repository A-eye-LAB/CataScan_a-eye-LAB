# 웹 구조

이 문서에서는 CataScan 웹 어드민의 전체적인 구조와 주요 컴포넌트에 대해 설명합니다.

## Front-End

### Development Environment

- **Library/Framework and Language**
  - React (18.3.1)
  - Next.js (15.1.2)
  - TypeScript (5^)
- **Networking**
  - SWR (2.3.0) (https://github.com/vercel/swr)
- **Styling**
  - Tailwind CSS (3.4.1) (https://tailwindcss.com/)
- **UI Library**
  - Shadcn/ui (https://ui.shadcn.com/)
  - Radix UI (https://www.radix-ui.com/)
- **Code Formatting**
  - Prettier (https://github.com/prettier/prettier)

### Directory Structure

```bash
.
├── public: 지속적으로 쓰이는 이미지 애셋 등
├── src
│   ├── app
│   │   ├── (admin): 어드민 주요 페이지와 관련된 컴포넌트, 페이지
│   │   ├── api: next 내부 api 기능을 사용하기 위한 디렉토리 / (예시) NextAuth 로직 등
│   │   ├── login
│   │   └── page.tsx
│   ├── components
│   │   ├── common: 여러 곳에서 사용되는 공용 컴포넌트
│   │   ├── login: login 페이지에서 사용되는 컴포넌트
│   │   ├── patients: patients 〃
│   │   ├── reports: reports 〃
│   │   └── ui: 공용 ui 컴포넌트 (shadcn/ui)
│   ├── context
│   ├── hooks: 각종 Custom Hooks
│   │   ├── api: API와 관련된 Hooks
│   └── lib
│       ├── api: API와 관련된 모듈
│       ├── constants: 각종 상수
│       ├── types: 각종 타입
│       └── utils: 유틸리티 모듈
├── tailwind.config.ts
└── tsconfig.json
```

### 주요 특징

- 로그인 페이지를 제외한 모든 페이지는 Client Side Rendering으로 제공됩니다.
- 주된 비즈니스 로직은 백엔드 API로 동작되며, 프론트엔드는 다양한 데이터를 보기 좋게 전달/관리하는 역할을 합니다.
- Shadcn/ui의 컴포넌트의 원래 그대로 형태를 사용하는 방향으로 작업하였습니다.
- Admin의 페이지는 리스트 페이지/상세 페이지로 나뉩니다.
  - 예컨대 `/reports`는 리스트 페이지, `/reports/[reportId]`는 상세페이지로 접속되는 형태입니다.
  - 상세 페이지에선 한번에 2개의 페이지가 렌더링되도록 [Parellel Routes](https://nextjs.org/docs/app/building-your-application/routing/parallel-routes) 방식을 이용하였습니다.


## Back-end

### Development Environment
- **Library/Framework and Language**
  - Java 17 (Eclipse Temurin)
  - Spring Boot (3.4.0)
- **Web Application Server**
  - Apache Tomcat (10.1.36)
- **Build Tool**
  - Maven (3.9.9)
- **Container Service**
  - Docker
  - Kubernetes
- **Monitoring**
  - Prometheus (3.2.1)
  - Grafana (11.5.2)


### Directory Structure

```bash
.
├── k3s: 쿠버네티스 이용 배포 파일 모음  
├── jmx: 자바 JMX 모니터링 관련 파일
├── src: 자바 소스코드 모음
│    └── app.main.java.org.cataract.web  
│        ├── application.service: 서비스계층 소스코드 모음
│        ├── bootstrap: 어플리케이션 시작시 구동되는 소스코드 모음
│        ├── config: 설정 관련 소스코드 모음
│        ├── domain: 엔티티, 도메인, 예외처리 관련 소스코드 모음
│        ├── helper: 날짜 변환, 유효성 관련 등 유틸 소스코드 모음
│        ├── infra: 레포지토리 계층 코드 모음
│        └── presentation: 컨트롤러 계층 및 요청/응답바디를 포함하는 Dto등 
│
├── docker-compose.yml: 개발서버용 도커컴포즈 환경 구성
├── Dockerfile: API서버 컨테이너 이미지 빌드용 Dockerfile
├── docker-compose-light.yml: 개발서버용 프로메테우스와 그라파나 컨테이너가 없는 도커컴포즈 환경 구성
├── Dockerfile-light: 별도 모니터링 없는 API서버 컨테이너 이미지 빌드용 Dockerfile
├── prometheus.yml: 개발서버용 Prometheus 환경 구성
└── pom.xml: 자바 라이브러리 구성(Maven) 


```


### 주요 특징
- 개발/테스트용 환경 구성은 Docker-compose를 이용해서 테스트해볼 수 있도록 단순히 EC2인스턴스 하나로 docker-compose를 통해서 모니터링까지 할 수 있도록 구성했습니다. 
- 모니터링은 Grafana와 Prometheus를 통해 API 서버 관련 사항을 모니터링할 수 있도록 구성했습니다. 
- 단일노드 클러스터로 작동하기 위해 경량형 쿠버네티스인 k3s를 활용했습니다.
- CI/CD는 별도 구성으로 동작할 수 있습니다. 


[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 빌드 및 배포](01_build_and_deployment.md) |
[다음: API](03_api.md)