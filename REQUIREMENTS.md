# Feature 1.1: User Authentication System

## Overview

Implement a complete user authentication system using Spring Security and JWT to secure the SmartMES application.

---

## Functional Requirements

### FR-1.1.1 User Login

**Description:** Users can log in with username and password to obtain access token.

**API Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (Success):**
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": "U001",
      "username": "admin",
      "realName": "System Admin",
      "role": "ADMIN"
    }
  }
}
```

**Response (Failure):**
```json
{
  "code": 401,
  "message": "Invalid username or password",
  "data": null
}
```

**Acceptance Criteria:**
- [ ] Valid credentials return JWT token
- [ ] Invalid credentials return 401 error
- [ ] Account locked after 5 failed attempts
- [ ] Login attempt logged in audit_log table

---

### FR-1.1.2 Token Refresh

**Description:** Refresh expired access token using refresh token.

**API Endpoint:** `POST /api/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600
  }
}
```

**Acceptance Criteria:**
- [ ] Valid refresh token returns new access token
- [ ] Expired refresh token returns 401 error
- [ ] Refresh token rotation implemented (optional)

---

### FR-1.1.3 User Logout

**Description:** Invalidate current user session.

**API Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "code": 200,
  "message": "Logout successful",
  "data": null
}
```

**Acceptance Criteria:**
- [ ] Token added to blacklist
- [ ] Subsequent requests with same token rejected
- [ ] Logout event logged

---

### FR-1.1.4 Get Current User

**Description:** Retrieve current logged-in user information.

**API Endpoint:** `GET /api/auth/me`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": "U001",
    "username": "admin",
    "realName": "System Admin",
    "role": "ADMIN",
    "team": "IT Department",
    "status": "ACTIVE"
  }
}
```

---

### FR-1.1.5 Password Management

**Description:** User can change their own password.

**API Endpoint:** `PUT /api/auth/password`

**Request:**
```json
{
  "oldPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**Acceptance Criteria:**
- [ ] Old password must be verified
- [ ] New password must meet complexity requirements (min 8 chars, 1 uppercase, 1 number)
- [ ] Password change logged in audit_log

---

## Non-Functional Requirements

### NFR-1.1.1 Security
- Passwords stored using BCrypt (strength 10+)
- JWT signed with HS256 algorithm
- Access token expires in 1 hour
- Refresh token expires in 7 days
- HTTPS required in production

### NFR-1.1.2 Performance
- Login response time < 500ms
- Token validation < 50ms

### NFR-1.1.3 Reliability
- Support token blacklist for logout
- Handle concurrent login sessions

---

## Technical Design

### Backend Components

```
smartmes-backend/src/main/java/com/smartmes/
├── config/
│   └── SecurityConfig.java          # Spring Security configuration
├── controller/
│   └── AuthController.java          # Authentication endpoints
├── service/
│   ├── AuthService.java             # Authentication service interface
│   └── impl/
│       └── AuthServiceImpl.java     # Authentication implementation
├── security/
│   ├── JwtTokenProvider.java        # JWT generation and validation
│   ├── JwtAuthenticationFilter.java # Request filter
│   └── UserDetailsServiceImpl.java  # Load user from database
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RefreshTokenRequest.java
│   └── ChangePasswordRequest.java
└── repository/
    └── TokenBlacklistRepository.java # (Optional) Redis-based
```

### Frontend Components

```
smartmes-frontend/src/
├── views/
│   └── Auth/
│       └── LoginView.vue            # Login page
├── store/
│   └── auth.js                      # Auth state management
├── api/
│   └── auth.js                      # Auth API calls
├── router/
│   └── guards.js                    # Route guards
└── utils/
    └── token.js                     # Token storage utilities
```

### Database Changes

```sql
-- Add columns to user table if not exists
ALTER TABLE user ADD COLUMN last_login_time DATETIME;
ALTER TABLE user ADD COLUMN login_attempts INT DEFAULT 0;
ALTER TABLE user ADD COLUMN locked_until DATETIME;

-- Token blacklist table (if not using Redis)
CREATE TABLE token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at)
);
```

---

## Dependencies

### Backend (pom.xml)
```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### Configuration (application.yml)
```yaml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here}
  access-token-expiration: 3600000  # 1 hour
  refresh-token-expiration: 604800000  # 7 days
```

---

## Test Cases

### Unit Tests
- [ ] JwtTokenProvider: token generation and validation
- [ ] AuthService: login, logout, refresh token
- [ ] Password encoding and verification

### Integration Tests
- [ ] Login with valid credentials
- [ ] Login with invalid credentials
- [ ] Access protected endpoint with valid token
- [ ] Access protected endpoint with expired token
- [ ] Token refresh flow
- [ ] Logout and token invalidation

### Frontend Tests
- [ ] Login form validation
- [ ] Token storage and retrieval
- [ ] Auto-redirect to login when unauthorized
- [ ] Token refresh on expiration

---

## UI Mockup

### Login Page
```
┌─────────────────────────────────────┐
│           SmartMES Lite             │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Username                    │   │
│  │  __________________________ │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Password                    │   │
│  │  __________________________ │   │
│  └─────────────────────────────┘   │
│                                     │
│  ☐ Remember me                      │
│                                     │
│  ┌─────────────────────────────┐   │
│  │         Login               │   │
│  └─────────────────────────────┘   │
│                                     │
│  Forgot password?                   │
└─────────────────────────────────────┘
```

---

## Checklist

- [ ] Backend: SecurityConfig.java created
- [ ] Backend: JwtTokenProvider.java implemented
- [ ] Backend: AuthController.java with all endpoints
- [ ] Backend: AuthService.java with business logic
- [ ] Backend: Password encryption with BCrypt
- [ ] Frontend: LoginView.vue created
- [ ] Frontend: Auth store implemented
- [ ] Frontend: Route guards added
- [ ] Frontend: Token auto-refresh logic
- [ ] Tests: Unit tests written
- [ ] Tests: Integration tests passed
- [ ] Documentation: API docs updated
