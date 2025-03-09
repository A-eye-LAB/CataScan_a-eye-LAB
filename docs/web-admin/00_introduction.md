# 웹 어드민 소개

이 문서에서는 CataScan 웹 어드민에 대한 전반적인 내용을 설명합니다.

## 목차

- [빌드 및 배포](01_build_and_deployment.md)
- [웹 구조](02_web_architecture.md)
- [API](03_api.md)
- [데이터베이스](04_database.md)
- [사용자 가이드](05_user_guide.md)

[처음으로](../overview.md) |
[다음: 빌드 및 배포](01_build_and_deployment.md)

## 개요

CataScan은 개인과 의료 종사자를 위해 개발된 백내장 검출 시스템으로, Android와 iOS 플랫폼을 지원합니다. 내장된 AI 모델을 통해 눈 이미지에서 백내장 여부를 신속하게 분석할 수 있습니다. 본 Admin은 CataScan 앱으로 찍은 데이터를 관리하기 위한 목적으로 만들어졌습니다.

## 주요 기능

- 레포트 관리 기능
  - 앱으로부터 받은 진단 결과를 확인, AI 결과와 더불어 의료진의 진단 결과 저장 기능
- 환자 관리 기능
  - 환자 기본 정보 관리: 신규 등록/수정/삭제
- 레포트 - 환자 연동 기능
  - 생성된 레포트를 이미 존재하는 환자에 연동하는 기능
- 유저 관리 기능
  - 기관 별 분류를 위한 유저 관리 기능(신규 등록/수정/삭제)

## 참고 링크

- [Admin Figma](https://www.figma.com/design/c0vjaijleDJP3TRMa1rMmt/a-eye-lab_product?node-id=0-1)
