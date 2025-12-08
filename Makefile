# SmartMES Lite Makefile
# 提供便捷的命令来管理Docker服务

.PHONY: help build up down restart logs clean dev prod ps backup restore

# 默认目标：显示帮助信息
help:
	@echo "SmartMES Lite - Available Commands:"
	@echo ""
	@echo "  make build          - Build all Docker images"
	@echo "  make up             - Start all services (default mode)"
	@echo "  make dev            - Start all services in development mode"
	@echo "  make prod           - Start all services in production mode"
	@echo "  make down           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs from all services"
	@echo "  make logs-backend   - View backend logs"
	@echo "  make logs-frontend  - View frontend logs"
	@echo "  make logs-mysql     - View MySQL logs"
	@echo "  make ps             - Show service status"
	@echo "  make clean          - Stop and remove all containers and volumes"
	@echo "  make backup         - Backup MySQL database"
	@echo "  make restore        - Restore MySQL database from backup"
	@echo "  make init-db        - Initialize database"
	@echo "  make test-backend   - Run backend tests"
	@echo "  make test-frontend  - Run frontend tests"
	@echo ""

# 构建所有镜像
build:
	@echo "Building Docker images..."
	docker-compose build --no-cache

# 启动服务（默认模式）
up:
	@echo "Starting services in default mode..."
	docker-compose up -d
	@echo "Services started! Access at http://localhost"

# 开发模式
dev:
	@echo "Starting services in development mode..."
	docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
	@echo "Services started in dev mode! Access at http://localhost"

# 生产模式
prod:
	@echo "Starting services in production mode..."
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
	@echo "Services started in production mode!"

# 停止服务
down:
	@echo "Stopping services..."
	docker-compose down

# 重启服务
restart:
	@echo "Restarting services..."
	docker-compose restart

# 查看日志
logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend

logs-mysql:
	docker-compose logs -f mysql

# 查看服务状态
ps:
	docker-compose ps

# 清理所有容器和卷
clean:
	@echo "WARNING: This will remove all containers, volumes, and data!"
	@read -p "Are you sure? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		docker-compose down -v --remove-orphans; \
		echo "Cleanup completed!"; \
	else \
		echo "Cleanup cancelled."; \
	fi

# 备份数据库
backup:
	@echo "Creating database backup..."
	@mkdir -p backups
	@BACKUP_FILE=backups/smartmes_$$(date +%Y%m%d_%H%M%S).sql; \
	docker-compose exec -T mysql mysqldump -u smartmes -psmartmes123 smartmes > $$BACKUP_FILE; \
	echo "Backup created: $$BACKUP_FILE"

# 恢复数据库
restore:
	@echo "Available backups:"
	@ls -lh backups/*.sql 2>/dev/null || echo "No backups found"
	@read -p "Enter backup filename: " BACKUP_FILE; \
	if [ -f "$$BACKUP_FILE" ]; then \
		docker-compose exec -T mysql mysql -u smartmes -psmartmes123 smartmes < $$BACKUP_FILE; \
		echo "Database restored from $$BACKUP_FILE"; \
	else \
		echo "Backup file not found: $$BACKUP_FILE"; \
	fi

# 初始化数据库
init-db:
	@echo "Initializing database..."
	cd database && ./init-db.sh

# 运行后端测试
test-backend:
	@echo "Running backend tests..."
	docker-compose exec backend mvn test

# 运行前端测试
test-frontend:
	@echo "Running frontend tests..."
	docker-compose exec frontend npm test

# 进入容器shell
shell-backend:
	docker-compose exec backend bash

shell-frontend:
	docker-compose exec frontend sh

shell-mysql:
	docker-compose exec mysql bash

# 查看资源使用情况
stats:
	docker stats smartmes-backend smartmes-frontend smartmes-mysql

# 检查健康状态
health:
	@echo "Checking service health..."
	@curl -f http://localhost:8080/actuator/health && echo " - Backend: Healthy" || echo " - Backend: Unhealthy"
	@curl -f http://localhost/ > /dev/null 2>&1 && echo " - Frontend: Healthy" || echo " - Frontend: Unhealthy"
