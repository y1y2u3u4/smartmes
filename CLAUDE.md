# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SmartMES Lite is a Manufacturing Execution System for semiconductor production tracking. It's a full-stack application with a Spring Boot backend, Vue 3 frontend, and MySQL database.

## Development Commands

### Frontend (smartmes-frontend/)
```bash
npm run dev      # Start dev server at http://localhost:5173
npm run build    # Production build
npm run lint     # ESLint with auto-fix
```

### Backend (smartmes-backend/)
```bash
./mvnw spring-boot:run           # Run application (port 8080)
./mvnw clean package             # Build JAR
./mvnw test                      # Run tests
./mvnw test -Dtest=ClassName     # Run single test class
```

### Docker
```bash
docker-compose up -d             # Start all services (MySQL, backend, frontend)
docker-compose -f docker-compose.dev.yml up -d  # Development mode
docker-compose down              # Stop all services
```

### Database
- MySQL 8.0, database name: `smartmes_lite` (local) or `smartmes` (Docker)
- Schema: `database/schema.sql`
- Initial data: `database/init_data.sql`

## Architecture

### Backend (Spring Boot 3.2 / Java 17)
- **API prefix**: `/api` (configured in application.yml)
- **Data access**: Hybrid JPA + MyBatis
  - JPA repositories for simple CRUD (`repository/`)
  - MyBatis mappers for complex queries (`mapper/`, XML in `mapper/xml/`)
- **Package structure**:
  - `controller/` - REST endpoints
  - `service/` + `service/impl/` - Business logic
  - `entity/` - JPA entities
  - `dto/` - Data transfer objects
  - `enums/` - Status enums (DowntimeStatus, DowntimeType)

### Frontend (Vue 3 + Vite)
- **UI**: Element Plus components
- **Charts**: ECharts
- **Routing**: Vue Router with lazy loading
- **API client**: Axios with interceptors (`src/api/request.js`)
- **Path alias**: `@` maps to `src/`
- **Proxy**: `/api` proxied to `http://localhost:3000` in dev

### Key Domain Modules
1. **Work Orders** (`/workorders`) - Production order lifecycle (PENDING → IN_PROGRESS → COMPLETED)
2. **Downtime** (`/downtime`) - Equipment fault reporting and tracking
3. **Dashboard** (`/dashboard`) - Production overview with ECharts visualizations

### API Response Format
All backend APIs return wrapped responses:
```java
Result<T> { code, message, data, timestamp }
PageResult<T> { content, totalElements, totalPages, pageNumber, pageSize }
```

## Database Tables
- `product` - Product catalog
- `equipment` - Equipment registry with status
- `work_order` - Production orders
- `downtime_report` - Equipment downtime records
- `user` - User accounts with roles
- `audit_log` - Operation audit trail
