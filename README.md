# File Extension Blocker Backend

파일 확장자 차단 백엔드 API 서버입니다.

## 주요 기능

- **JWT 기반 인증**: 로그인/로그아웃 기능
- **고정 확장자 관리**: 시스템에서 미리 정의된 확장자들의 차단 설정
- **커스텀 확장자 관리**: 사용자별 커스텀 확장자 추가/삭제

## 기술 스택

- **Spring Boot 3.2.3**
- **Spring Security** (JWT 인증)
- **Spring Data JPA**
- **PostgreSQL**
- **Gradle**
- **Java 17**

## 로컬 실행

```bash
docker compose up --build -d
```

## 통신

### API 주소

```http
localhost:8080, 127.0.0.1:8080
```

### Swagger UI

```http
http://localhost:8080/swagger-ui/index.html
```

## 테스트 계정

- 여러 사용자가 파일 확장자 필터 시스템을 이용하는 상황을 가정하고, 독립된 데이터처리를 보여주고자 로그인 기능을 구현했습니다.

데이터베이스에 미리 생성되는 테스트 계정들:

- `1234@example.com` / `1234`
- `2345@example.com` / `2345`
- `3456@example.com` / `3456`
- `4567@example.com` / `4567`
- `5678@example.com` / `5678`
-

## API 엔드포인트

### 인증 API

#### 로그인

```http
POST /api/auth/login
Content-Type: application/json

request body
{
  "email": "1234@example.com",
  "password": "1234"
}

response body
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "uuid",
  "email": "1234@example.com",
  "username": "testuser"
}
```

#### 로그아웃

```http
POST /api/auth/logout
Authorization: Bearer {token}
```

### 확장자 관리 API

- 모든 확장자 API는 JWT 토큰이 필요합니다.
- 각 유저의 인증정보를 바탕으로 유저의 id를 획득하고 각자 독립된 작업이 가능하도록 했습니다.

#### 모든 확장자 목록 조회

- 유저가 로그인을 마친 이후 파일 확장자 필터 화면에 진입할 때 유저와 관련있는 모든 확장자 데이터를 조회하기 위해 구현했습니다.

```http
GET /api/extensions
Authorization: Bearer {token}

response body

{
    "fixed": [
        {
            "id": "0ab8af46-824c-4362-9b18-49636631729c",
            "extension": "bat",
            "displayName": "Batch File",
            "blocked": false
        },....
    ],
    "custom": [
        {
            "id": "22bc5119-bd8e-4d6a-8c12-5f895cc24a0b",
            "extension": "3456-1"
        },....
    ]
}

```

#### 고정 확장자 설정 변경

- 고정 확장자는 요구사항에 따라 그 종류가 변하지 않습니다.
- 따라서 POST 요청을 통해 유저가 차단한 고정 확장자에 대한 데이터는 fixed_blocked_extensions 테이블에 저장/삭제만 (check/unCheck 기능만 가지므로) 가능하도록 설계했습니다.
- 화면에서 비동기 요청을 할 수 있도록 독립된 요청으로 구현했습니다.

```http
POST /api/extensions/fixed/:{fixed.id}
Authorization: Bearer {token}
Content-Type: application/json

#request body

{
  "blocked": true
}

#response body

{
    "changed": true,
    "blocked": true
}
```

#### 커스텀 확장자 추가

- 커스텀 확장자 추가 요청에서 유효성 검증이 끝난 커스텀 확장자는 새로운 데이터이기에 생성만 합니다.
- response body 엔 데이터 생성 성공 여부, 매세지 등을 함께 전달합니다.
- 역시 화면에서의 비동기 호출을 위해 독립된 요청으로 구현했습니다.

```http
POST /api/extensions/custom
Authorization: Bearer {token}
Content-Type: application/json

#request body

{
  "extension": "xyz"
}

#response body

{
    "success": true,
    "id": "68cadab6-b1ad-44be-8940-1725bf8d8f72",
    "extension": "1234-5",
    "message": "추가 완료"
}
```

#### 커스텀 확장자 삭제

- 화면에서 성공 여부를 판단하고 비동기적으로 요청을 처리할 수 있도록 구현했습니다.

```http
DELETE /api/extensions/custom/{extensionId}
Authorization: Bearer {token}

response body
{
    "success": true,
    "extensionId": "c91d72c7-ffd8-4793-8c3b-6103bfe3bcbb",
    "deletedExtension": "1234-9"
}

```

## 프로젝트 구조

```https
.
├── postman/Flow.postman_collection.json                          # Postman 컬렉션: 로컬 API 테스트시 import 하여 사용할 수 있습니다.
├── Dockerfile                                                    # 백엔드 컨테이너 빌드를 위한 도커 파일
├── README.md
├── docker-compose.yml                                            # 백엔드 + 데이터베이스 구성을 위한 Docker Compose
├── init.db
│   └── seed.sql                                                  # docker compose 를 이용한 로컬 개발환경을 구성할 때 사용하는 데이터베이스 초기화 데이터
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── flow                                      # 백엔드 어플리케이션
    │   │               ├── FlowApplication.java
    │   │               ├── config                                # JWT 인증을 위한 설정
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   ├── JwtConfig.java
    │   │               │   ├── OpenApiConfig.java
    │   │               │   └── SecurityConfig.java
    │   │               ├── controller                            # 인증, 확장자 관리 API 컨트롤러
    │   │               │   ├── AuthController.java
    │   │               │   └── ExtensionController.java
    │   │               ├── domain                                # 도메인 모델 (사용자, 시스템 기본 설정 - 요구사항에 명시된 제한 사항을 데이터베이스에 저장된 데이터를 이용하도록 했습니다, 고정 확장자, 유저 별 고정/커스텀 확장자)
    │   │               │   ├── FixedBlockedExtension.java
    │   │               │   ├── SystemSetting.java
    │   │               │   ├── User.java
    │   │               │   ├── UserCustomBlockedExtension.java
    │   │               │   ├── UserExtension.java
    │   │               │   └── UserFixedBlockedExtension.java
    │   │               ├── dto                                   # 자료형
    │   │               │   ├── AddCustomExtensionRequest.java
    │   │               │   ├── AddCustomExtensionResponse.java
    │   │               │   ├── CustomExtensionDto.java
    │   │               │   ├── CustomExtensionListResponse.java
    │   │               │   ├── DeleteCustomExtensionRequest.java
    │   │               │   ├── DeleteCustomExtensionResponse.java
    │   │               │   ├── FixedExtensionDto.java
    │   │               │   ├── LoginRequest.java
    │   │               │   ├── LoginResponse.java
    │   │               │   ├── UpdateFixedExtensionRequest.java
    │   │               │   ├── UpdateFixedExtensionResponse.java
    │   │               │   └── UserExtensionsResponse.java
    │   │               ├── exception                              # 전역 예외 처리
    │   │               │   └── GlobalExceptionHandler.java
    │   │               ├── repository
    │   │               │   ├── FixedBlockedExtensionRepository.java
    │   │               │   ├── SystemSettingRepository.java
    │   │               │   ├── UserCustomBlockedExtensionRepository.java
    │   │               │   ├── UserExtensionsRepository.java
    │   │               │   ├── UserFixedBlockedExtensionRepository.java
    │   │               │   └── UserRepository.java
    │   │               ├── service                                # 비즈니스 로직
    │   │               │   ├── AuthService.java
    │   │               │   └── ExtensionService.java
    │   │               └── util
    │   │                   └── JwtUtil.java
...
```
