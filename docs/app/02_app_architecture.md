# 앱 구조

이 문서에서는 CataScan 모바일 앱의 전체적인 구조와 주요 컴포넌트에 대해 설명합니다.

[처음으로](../overview.md) | 
[소개로](00_introduction.md) | 
[이전: 빌드 방법](01_how_to_build.md) | 
[다음: API](03_api.md) 

# App 구조

이 문서에서는 CataScan App의 전체적인 구조와 주요 컴포넌트에 대해 설명합니다.

## Directory Structure

```bash
.
├── android: 안드로이드 플랫폼 설정
├── assets: 정적 리소스
│   ├── vector: 벡터 이미지
│   ├── raster: 래스터 이미지
│   ├── model: ONNX 모델 파일
│   ├── lottie: 애니메이션 파일
│   ├── logo: 로고 이미지
│   ├── icon: 아이콘 이미지
│   └── font: 폰트 파일
├── ios: iOS 플랫폼 설정
├── lib
│   ├── core: 핵심 기능 및 공통 유틸리티
│   │   ├── providers: 전역 상태 관리
│   │   ├── utils: 유틸리티 함수
│   │   ├── styles: 앱 스타일 정의
│   │   ├── exceptions: 예외 처리
│   │   └── constants: 상수 정의
│   ├── models: 데이터 모델 클래스
│   │   ├── user_info.dart: 사용자 정보 모델
│   │   ├── patient_info.dart: 환자 정보 모델
│   │   ├── eye_scan_data.dart: 눈 스캔 데이터 모델
│   │   ├── prediction_result.dart: 진단 결과 모델
│   │   └── processed_image.dart: 처리된 이미지 모델
│   ├── services: 비즈니스 로직 및 API 통신
│   │   ├── api: API 통신 관련 서비스
│   │   ├── login: 로그인 관련 서비스
│   │   ├── user_info: 사용자 정보 관리
│   │   ├── patient_info: 환자 정보 관리
│   │   ├── eye_detection: 눈 감지 서비스
│   │   ├── cataract_diagnosis: 백내장 진단 서비스
│   │   ├── image: 이미지 처리 서비스
│   │   └── storage: 데이터 저장 서비스
│   └── views: UI 컴포넌트 및 화면
│       ├── welcome_screen.dart: 시작 화면
│       ├── common: 공통 UI 컴포넌트
│       │   ├── survey: 설문 관련 컴포넌트
│       │   ├── analysis: 분석 관련 컴포넌트
│       │   ├── camera: 카메라 관련 컴포넌트
│       │   ├── user_info: 사용자 정보 관련 컴포넌트
│       │   ├── logout_dialog.dart: 로그아웃 다이얼로그
│       │   └── button.dart: 공통 버튼 컴포넌트
│       ├── user: 사용자 관련 화면
│       └── center: 센터 관련 화면
└── test: 테스트 코드 
