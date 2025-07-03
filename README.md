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

## docker-comppose를 이용하여 로컬에서 기동했을 때의 API 요청 포트

localhost:8080, 127.0.0.1:8080

## API 엔드포인트

### 인증 API

#### 로그인

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "1234@example.com",
  "password": "1234"
}
```

응답:

```json
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

모든 확장자 API는 JWT 토큰이 필요합니다.

#### 고정 확장자 목록 조회

```http
GET /api/extensions/fixed
Authorization: Bearer {token}
```

#### 고정 확장자 설정 변경

```http
POST /api/extensions/fixed
Authorization: Bearer {token}
Content-Type: application/json

{
  "extension": "exe",
  "blocked": true
}
```

#### 커스텀 확장자 목록 조회

```http
GET /api/extensions/custom
Authorization: Bearer {token}
```

#### 커스텀 확장자 추가

```http
POST /api/extensions/custom
Authorization: Bearer {token}
Content-Type: application/json

{
  "extension": "xyz"
}
```

#### 커스텀 확장자 삭제

```http
DELETE /api/extensions/custom/{extension}
Authorization: Bearer {token}
```

## 로컬 실행 방법

### 1. gradle 빌드

```bash
./gradlew build
```

### 2. docker compose 실행

```bash
docker-compose up --build -d
```

## 설정

### JWT 설정

`application.properties`에서 JWT 관련 설정을 변경할 수 있습니다:

```properties
jwt.secret=your-secret-key
jwt.expiration=86400000
jwt.issuer=flow-extension-blocker
```

## 테스트 계정

데이터베이스에 미리 생성되는 테스트 계정들:

- `1234@example.com` / `1234`
- `2345@example.com` / `2345`
- `3456@example.com` / `3456`
- `4567@example.com` / `4567`
- `5678@example.com` / `5678`
