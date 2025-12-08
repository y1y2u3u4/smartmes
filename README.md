# SmartMES Lite - 轻量级制造执行系统

SmartMES Lite is a lightweight Manufacturing Execution System designed for small to medium-sized manufacturing enterprises. It provides essential MES functionalities including order management, production planning, work order tracking, quality control, and equipment management.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Manual Setup](#manual-setup)
- [Development Guide](#development-guide)
- [API Documentation](#api-documentation)
- [Default Credentials](#default-credentials)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

- **Order Management**: Create, track, and manage production orders
- **Production Planning**: Schedule and plan production activities
- **Work Order Tracking**: Real-time work order status monitoring
- **Quality Control**: Quality inspection and defect tracking
- **Equipment Management**: Equipment status monitoring and maintenance records
- **User Management**: Role-based access control and user authentication

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: MyBatis
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI

### Frontend
- **Framework**: Vue 3
- **Build Tool**: Vite
- **UI Library**: Element Plus
- **State Management**: Pinia
- **HTTP Client**: Axios
- **Routing**: Vue Router

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Web Server**: Nginx
- **Build Tool**: Maven (Backend), npm (Frontend)

## Prerequisites

Before running SmartMES Lite, ensure you have the following installed:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Operating System**: Linux, macOS, or Windows with WSL2

To check if Docker is installed:
```bash
docker --version
docker-compose --version
```

## Quick Start

### One-Command Startup (Recommended)

1. **Clone the repository**:
```bash
git clone <repository-url>
cd test_demo
```

2. **Run the startup script**:
```bash
# Default mode (production)
./start.sh

# Development mode (with hot reload)
./start.sh dev

# Production mode (with resource limits)
./start.sh prod
```

3. **Access the application**:
   - Frontend: http://localhost
   - Backend API: http://localhost:8080/api
   - API Documentation: http://localhost:8080/swagger-ui.html

4. **Login with default credentials**:
   - Username: `admin`
   - Password: `admin123`

### Clean Up

To stop and remove all containers:
```bash
./start.sh clean
```

Or manually:
```bash
docker-compose down -v
```

## Manual Setup

If you prefer to run commands manually:

### 1. Configure Environment

```bash
# Copy environment configuration
cp .env.example .env

# Edit configuration if needed
nano .env
```

### 2. Start Services

**Default Mode**:
```bash
docker-compose up -d --build
```

**Development Mode**:
```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

**Production Mode**:
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

### 3. Check Service Status

```bash
# View all services
docker-compose ps

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### 4. Initialize Database (if needed)

```bash
cd database
./init-db.sh
```

## Development Guide

### Backend Development

#### Local Development Setup

1. **Navigate to backend directory**:
```bash
cd smartmes-backend
```

2. **Install dependencies**:
```bash
mvn clean install
```

3. **Run locally** (requires MySQL running):
```bash
mvn spring-boot:run
```

#### Remote Debugging

When running in development mode with Docker:
- Debug port: `5005`
- Configure your IDE to connect to `localhost:5005`

#### Backend Structure
```
smartmes-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartmes/
│   │   │       ├── controller/    # REST Controllers
│   │   │       ├── service/       # Business Logic
│   │   │       ├── mapper/        # MyBatis Mappers
│   │   │       ├── entity/        # Entity Classes
│   │   │       ├── dto/           # Data Transfer Objects
│   │   │       ├── config/        # Configuration
│   │   │       └── security/      # Security Configuration
│   │   └── resources/
│   │       ├── mapper/            # MyBatis XML
│   │       └── application.yml    # Configuration
│   └── test/                      # Unit Tests
├── Dockerfile                     # Production Dockerfile
├── Dockerfile.dev                 # Development Dockerfile
└── pom.xml                        # Maven Configuration
```

### Frontend Development

#### Local Development Setup

1. **Navigate to frontend directory**:
```bash
cd smartmes-frontend
```

2. **Install dependencies**:
```bash
npm install
```

3. **Run development server**:
```bash
npm run dev
```

4. **Build for production**:
```bash
npm run build
```

#### Frontend Structure
```
smartmes-frontend/
├── src/
│   ├── api/              # API Service Layer
│   ├── assets/           # Static Assets
│   ├── components/       # Reusable Components
│   ├── router/           # Vue Router Configuration
│   ├── store/            # Pinia State Management
│   ├── views/            # Page Components
│   ├── utils/            # Utility Functions
│   ├── App.vue           # Root Component
│   └── main.js           # Entry Point
├── public/               # Public Assets
├── index.html            # HTML Template
├── vite.config.js        # Vite Configuration
├── Dockerfile            # Production Dockerfile
├── Dockerfile.dev        # Development Dockerfile
├── nginx.conf            # Nginx Configuration
└── package.json          # npm Configuration
```

## API Documentation

### Access API Documentation

When the application is running:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Main API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh token

#### Orders
- `GET /api/orders` - List all orders
- `GET /api/orders/{id}` - Get order details
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}` - Update order
- `DELETE /api/orders/{id}` - Delete order

#### Work Orders
- `GET /api/work-orders` - List all work orders
- `GET /api/work-orders/{id}` - Get work order details
- `POST /api/work-orders` - Create work order
- `PUT /api/work-orders/{id}/status` - Update status

#### Equipment
- `GET /api/equipment` - List all equipment
- `GET /api/equipment/{id}` - Get equipment details
- `POST /api/equipment` - Register equipment
- `PUT /api/equipment/{id}` - Update equipment

#### Quality
- `GET /api/quality/inspections` - List inspections
- `POST /api/quality/inspections` - Create inspection
- `GET /api/quality/defects` - List defects

#### Users
- `GET /api/users` - List users (Admin only)
- `POST /api/users` - Create user (Admin only)
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

## Default Credentials

### System Administrator
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: Administrator (full access)

### Production Manager
- **Username**: `manager`
- **Password**: `manager123`
- **Role**: Manager (view and edit production data)

### Operator
- **Username**: `operator`
- **Password**: `operator123`
- **Role**: Operator (view and update work orders)

**Note**: Please change these default passwords in production!

## Project Structure

```
test_demo/
├── smartmes-backend/          # Backend Spring Boot Application
│   ├── src/
│   ├── Dockerfile
│   ├── Dockerfile.dev
│   └── pom.xml
├── smartmes-frontend/         # Frontend Vue Application
│   ├── src/
│   ├── Dockerfile
│   ├── Dockerfile.dev
│   ├── nginx.conf
│   └── package.json
├── database/                  # Database Scripts
│   ├── schema.sql            # Database Schema
│   ├── init_data.sql         # Initial Data
│   ├── my.cnf                # MySQL Configuration
│   └── init-db.sh            # Initialization Script
├── docs/                      # Documentation
├── logs/                      # Application Logs
├── backups/                   # Database Backups
├── docker-compose.yml         # Default Docker Compose
├── docker-compose.dev.yml     # Development Configuration
├── docker-compose.prod.yml    # Production Configuration
├── .env.example               # Environment Template
├── start.sh                   # Startup Script
└── README.md                  # This File
```

## Configuration

### Environment Variables

Key environment variables in `.env`:

```bash
# Database
MYSQL_ROOT_PASSWORD=smartmes123
MYSQL_DATABASE=smartmes
MYSQL_USER=smartmes
MYSQL_PASSWORD=smartmes123

# Ports
BACKEND_PORT=8080
FRONTEND_PORT=80
MYSQL_PORT=3306

# Security (Change in production!)
JWT_SECRET=your-secret-key-change-this-in-production
JWT_EXPIRATION=24

# Application
SPRING_PROFILES_ACTIVE=prod
TZ=Asia/Shanghai
```

### Backend Configuration

Edit `smartmes-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/smartmes
    username: smartmes
    password: smartmes123

server:
  port: 8080
```

### Frontend Configuration

Edit `smartmes-frontend/vite.config.js`:

```javascript
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://backend:8080',
        changeOrigin: true
      }
    }
  }
}
```

## Troubleshooting

### Common Issues

#### 1. MySQL Container Fails to Start

**Symptoms**: MySQL container exits immediately or shows connection errors.

**Solutions**:
```bash
# Remove existing volumes and restart
docker-compose down -v
docker-compose up -d

# Check MySQL logs
docker-compose logs mysql
```

#### 2. Backend Cannot Connect to Database

**Symptoms**: Backend logs show connection errors.

**Solutions**:
```bash
# Verify MySQL is healthy
docker-compose ps

# Check network connectivity
docker-compose exec backend ping mysql

# Verify database credentials in .env
```

#### 3. Frontend Shows API Errors

**Symptoms**: Frontend displays connection errors or 502 Bad Gateway.

**Solutions**:
```bash
# Check backend is running
docker-compose ps backend

# Verify Nginx configuration
docker-compose exec frontend cat /etc/nginx/nginx.conf

# Check backend logs
docker-compose logs backend
```

#### 4. Port Already in Use

**Symptoms**: Error binding to port 80, 8080, or 3306.

**Solutions**:
```bash
# Find process using the port
lsof -i :8080

# Change port in .env file
BACKEND_PORT=8081

# Restart services
docker-compose down
docker-compose up -d
```

#### 5. Slow Performance

**Solutions**:
```bash
# Check resource usage
docker stats

# Increase Docker resources in Docker Desktop settings
# Minimum recommended: 4GB RAM, 2 CPUs

# Use production mode for better performance
./start.sh prod
```

### Viewing Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend

# Last 100 lines
docker-compose logs --tail=100 backend

# Real-time logs
docker-compose logs -f --tail=0 backend
```

### Database Access

```bash
# Connect to MySQL container
docker-compose exec mysql mysql -u smartmes -psmartmes123 smartmes

# Backup database
docker-compose exec mysql mysqldump -u smartmes -psmartmes123 smartmes > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u smartmes -psmartmes123 smartmes < backup.sql
```

### Reset Everything

```bash
# Stop and remove all containers, volumes, and networks
docker-compose down -v --remove-orphans

# Remove all related images
docker rmi smartmes-backend smartmes-frontend

# Start fresh
./start.sh
```

## Useful Commands

### Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose stop

# Restart specific service
docker-compose restart backend

# View service status
docker-compose ps

# Execute command in container
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p

# Remove stopped containers
docker-compose rm
```

### Development Commands

```bash
# Rebuild specific service
docker-compose up -d --build backend

# View real-time resource usage
docker stats

# Inspect container
docker inspect smartmes-backend

# Copy files from container
docker cp smartmes-backend:/app/logs ./local-logs
```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- **Issues**: Please open an issue on GitHub
- **Documentation**: Check the `docs/` directory
- **Email**: support@smartmes.com

## Acknowledgments

- Spring Boot Team
- Vue.js Team
- Element Plus Team
- Docker Community

---

**SmartMES Lite** - Simplifying Manufacturing Execution
