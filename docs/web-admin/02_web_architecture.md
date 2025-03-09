# 웹 구조

이 문서에서는 CataScan 웹 어드민의 전체적인 구조와 주요 컴포넌트에 대해 설명합니다.

[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 빌드 및 배포](01_build_and_deployment.md) |
[다음: API](03_api.md)

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
