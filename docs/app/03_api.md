# API

이 문서에서는 CataScan 모바일 앱에서 사용하는 API에 대해 설명합니다.

[처음으로](../overview.md) | 
[소개로](00_introduction.md) | 
[이전: 앱 구조](02_app_architecture.md) | 
[다음: 사용자 가이드](04_user_guide.md) 

<br>

##### *발급된 토큰은 Paseto V4 형식이며, 이후 요청에서 Authorization 헤더에 Bearer Token으로 포함해야 합니다.

**로그인**
<details>
  <summary><code>POST</code> <code>/auth/login</code></summary>
  
### Request
- Content-Type: `application/json`
- Body 예시
  
  ```json
  {
    "username": "test",
    "password": "qwerty"
  }
  ```

### Response
- Content-Type: `application/json`
- Body 예시

  ```json
  {
    "token": "v4.local.hc2V0b190b2tlbg"
  }
  ```
  <br>
</details>

**현재 사용자 정보 요청**
<details>
  <summary><code>GET</code> <code>/user</code></summary>
  
### Request
- Authorization: `Bearer v4.local.hc2V0b190b2tlbg`

### Responses
- Content-Type: `application/json`
- Body 예시

  ```json
  {
    "id": 353,
    "username": "LabSDAdmin",
    "email": "cs@labsd.net",
    "role": "admin",
    "institutionName": "LabSD"
  }
  ```
<br>
</details>

**눈 이미지 등록**
<details>
  <summary><code>POST</code> <code>/reports</code></summary>
  
### Request
- Content-Type: `multipart/form-data`
- Authorization: `Bearer v4.local.hc2V0b190b2tlbg`
- Parameters:
  
  | Parameter | Type | Description |
  |-----------|------|-------------|
  | `leftImage` | File | 왼쪽 눈 이미지 파일 |
  | `rightImage` | File | 오른쪽 눈 이미지 파일 |
  | `imageId` | String | 이름-성별 형태(ex. john doe=m 이나 jane doe=f, anit kumar=o) |
  | `leftAiResult` | String | 왼쪽 눈 모델 판별결과 ('lowRisk' 또는 'requiresAttention') |
  | `rightAiResult` | String | 오른쪽 눈 모델 판별결과 ('lowRisk' 또는 'requiresAttention') |
### Response
- Content-Type: `application/json`
- Body 예시
  ```json
  {
    "reportId": 2,
    "leftImageFilePath": "/reports/images/20250206/jane smith=female-L-e0ac.jpg",
    "rightImageFilePath": "/reports/images/20250206/jane smith=female-R-e0ac.png",
    "scanDate": "2025-02-06 12:50:50",
    "leftAiResult": "lowRisk",
    "rightAiResult": "requiresAttention",
    "imageIdentifier": "jane smith=female"
  }
  ```
</details>
