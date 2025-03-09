# API

이 문서에서는 CataScan 웹 어드민에서 사용하는 API에 대해 설명합니다.

[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 웹 구조](02_web_architecture.md) |
[다음: 데이터베이스](04_database.md)

## Front-End

- GET 요청은 `SWR`을 이용하여 관리하고, 이외 요청은 `apiManager` 모듈을 이용해 관리합니다.
  - GET: `useSWR`을 직접 쓰지 않고 각 요청마다 custom hook을 만들어 사용합니다.
  - GET 요청과 관련된 Custom hook은 `/src/hooks/api/`에 생성합니다.
- SWR 이외 API 요청을 위한 별도 라이브러리는 없으며, `fetch` 기반으로 구현되어 있습니다.
- `restClient`: `get`, `post`, `patch`, `delete` 요청을 바로 쓰기 용이하도록 구현해둔 함수입니다.
