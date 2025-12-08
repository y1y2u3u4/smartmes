# SmartMES Lite Backend

SmartMES Lite Manufacturing Execution System - Backend API Service

## Overview

SmartMES Lite is a lightweight manufacturing execution system designed for small and medium-sized manufacturers. This backend service provides REST APIs for production management, equipment monitoring, and data analytics.

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Database access layer
- **MySQL**: 8.0+
- **Lombok**: Code generation
- **Maven**: Project management

## Project Structure

```
smartmes-backend/
├── src/main/java/com/smartmes/
│   ├── SmartMesApplication.java          # Main application entry
│   ├── config/                           # Configuration classes
│   │   └── WebConfig.java               # Web configuration (CORS, etc.)
│   ├── controller/                       # REST API controllers
│   │   ├── DashboardController.java     # Dashboard APIs
│   │   └── BaseDataController.java      # Base data management APIs
│   ├── service/                          # Service layer interfaces
│   │   ├── DashboardService.java        # Dashboard service
│   │   ├── EquipmentService.java        # Equipment service
│   │   ├── ProductService.java          # Product service
│   │   └── impl/                        # Service implementations
│   ├── repository/                       # Data access layer
│   │   ├── EquipmentRepository.java     # Equipment repository
│   │   ├── ProductRepository.java       # Product repository
│   │   ├── WorkOrderRepository.java     # Work order repository
│   │   └── DowntimeRepository.java      # Downtime repository
│   ├── entity/                           # JPA entities
│   │   ├── Equipment.java               # Equipment entity
│   │   ├── Product.java                 # Product entity
│   │   ├── WorkOrder.java               # Work order entity
│   │   └── Downtime.java                # Downtime/exception entity
│   └── dto/                              # Data Transfer Objects
│       ├── DashboardData.java           # Dashboard data wrapper
│       ├── ProductionOverview.java      # Production overview data
│       ├── DowntimeStatistics.java      # Downtime statistics data
│       ├── WorkOrderProgress.java       # Work order progress data
│       ├── EquipmentStatusData.java     # Equipment status data
│       └── ApiResponse.java             # Unified API response wrapper
└── src/main/resources/
    └── application.yml                   # Application configuration
```

## API Endpoints

### Dashboard APIs

Base URL: `/api/dashboard`

- `GET /overview` - Get production overview data
- `GET /downtime-stats` - Get downtime statistics
- `GET /workorder-progress` - Get work order progress
- `GET /equipment-status` - Get equipment status
- `GET /complete` - Get complete dashboard data

### Base Data Management APIs

Base URL: `/api/base-data`

#### Equipment Management

- `POST /equipment` - Create new equipment
- `PUT /equipment/{id}` - Update equipment
- `DELETE /equipment/{id}` - Delete equipment
- `GET /equipment/{id}` - Get equipment details
- `GET /equipment` - Get all equipment
- `GET /equipment/by-line/{lineId}` - Get equipment by production line

#### Product Management

- `POST /product` - Create new product
- `PUT /product/{id}` - Update product
- `DELETE /product/{id}` - Delete product
- `GET /product/{id}` - Get product details
- `GET /product` - Get all products
- `GET /product/search?name=xxx` - Search products by name

## Database Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Create database:
```sql
CREATE DATABASE smartmes_lite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. The application will auto-create tables on first run (using `spring.jpa.hibernate.ddl-auto=update`)

### Build and Run

1. **Build the project:**
```bash
mvn clean package
```

2. **Run the application:**
```bash
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/smartmes-backend-1.0.0.jar
```

3. **Access the API:**
- Base URL: `http://localhost:8080/api`
- Example: `http://localhost:8080/api/dashboard/overview`

## Data Models

### Equipment
- Equipment ID, Name, Type
- Production Line
- Status: RUNNING, IDLE, MAINTENANCE, FAULT
- Maintenance schedule

### Product
- Product Code, Name
- Specification, Type
- Unit, Standard work time
- Status: ACTIVE, INACTIVE

### Work Order
- Work Order No, Product info
- Plan/Actual quantity
- Status: PENDING, IN_PROGRESS, COMPLETED, ABNORMAL, CANCELLED
- Plan/Actual start/end time

### Downtime
- Downtime No, Equipment info
- Type: EQUIPMENT_FAULT, QUALITY_ISSUE, MATERIAL_SHORTAGE, TOOL_CHANGE, OTHER
- Start/End time, Duration
- Status: ONGOING, RESOLVED

## API Response Format

All APIs return data in the following format:

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

Error response:
```json
{
  "code": 500,
  "message": "Error message",
  "data": null
}
```

## Development

### Code Style

- Use Lombok annotations to reduce boilerplate code
- Follow standard Spring Boot project structure
- Add comprehensive Chinese comments for all classes and methods
- Use meaningful variable and method names

### Logging

- Use SLF4J with Lombok's @Slf4j
- Log level configuration in application.yml

## License

Copyright © 2024 SmartMES Team. All rights reserved.
