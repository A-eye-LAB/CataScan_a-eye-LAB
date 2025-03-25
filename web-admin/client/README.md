# CataScan Web Admin (Front-End)

## Development Environment

- **Library/Framework and Language**
    - React (18.3.1)
    - Next.js (15.1.2)
    - TypeScript (5^)
- **Networking**
    - SWR (2.3.0) (https://github.com/vercel/swr)
- **Styling**
    - Tailwind-css (3.4.1) (https://tailwindcss.com/)
- **UI Library**
    - Shadcn/ui (https://ui.shadcn.com/)
    - Radix UI (https://www.radix-ui.com/)
- **Code Formatting**
    - Prettier (https://github.com/prettier/prettier)

## Directory Structure

```bash
.
├── public: 지속적으로 쓰이는 이미지 애셋 등
├── src
│   ├── app
│   │   ├── (admin): 어드민 주요 페이지와 관련된 컴포넌트, 페이지
│   │   ├── api: next 내부 api 기능을 사용하기 위한 디렉토리 / (예시) Next-Auth 로직 등
│   │   ├── login
│   │   └── page.tsx
│   ├── components
│   │   ├── common: 여러 곳에서 사용되는 공용 컴포넌트
│   │   ├── login: login 페이지에서 사용되는 컴포넌트
│   │   ├── patients: patients 〃
│   │   ├── reports: reports 〃
│   │   └── ui: 공용 ui 컴포넌트
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

## Running Locally

- 로컬에서 정상적으로 실행하기 위해선 [.env.example](../../docs/web-admin/assets/.env.example)과 같은 형태의 파일이 필요합니다. Vercel을 이용하여 환경변수를 관리하길 추천합니다.
- 자세한 내용은 [링크](https://vercel.com/docs/concepts/projects/environment-variables#development-environment-variables)를 참고하세요.

1. Vercel CLI를 설치합니다: `npm i -g vercel`
2. Github 계정 등을 이용해 Vercel 서비스와 연결합니다 : `vercel link`
3. Vercel에 등록된 환경변수를 다운로드합니다: `vercel env pull`

```
npm install
npm run dev
```

## How to deploy

- Github에 프로젝트를 생성하고, Vercel에 연결하세요.
- 자세한 내용은 [Vercel 공식 문서](https://vercel.com/docs/deployments)를 참고하세요.

## 참고 링크

- [Admin Figma](https://www.figma.com/design/c0vjaijleDJP3TRMa1rMmt/a-eye-lab_product?node-id=0-1)
