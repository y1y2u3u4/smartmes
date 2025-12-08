#!/bin/bash

###############################################################################
# SmartMES Lite 一键启动脚本
# 用途：快速启动所有Docker服务并初始化数据库
# 使用：./start.sh [dev|prod]
###############################################################################

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的信息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 打印横幅
print_banner() {
    echo -e "${BLUE}"
    echo "=========================================="
    echo "  SmartMES Lite - 一键启动脚本"
    echo "=========================================="
    echo -e "${NC}"
}

# 检查Docker是否运行
check_docker() {
    print_info "Checking Docker status..."

    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi

    print_success "Docker is running."
}

# 检查docker-compose是否存在
check_docker_compose() {
    print_info "Checking Docker Compose..."

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    print_success "Docker Compose is available."
}

# 创建必要的目录
create_directories() {
    print_info "Creating necessary directories..."

    mkdir -p logs/backend
    mkdir -p logs/nginx
    mkdir -p backups

    print_success "Directories created."
}

# 复制环境配置文件
setup_env() {
    if [ ! -f .env ]; then
        print_info "Creating .env file from .env.example..."
        cp .env.example .env
        print_success ".env file created. Please review and modify if needed."
    else
        print_info ".env file already exists."
    fi
}

# 停止并清理现有容器
cleanup() {
    print_info "Stopping and removing existing containers..."
    docker-compose down -v 2>/dev/null || true
    print_success "Cleanup completed."
}

# 启动服务
start_services() {
    local env=$1
    print_info "Starting services in $env mode..."

    if [ "$env" = "dev" ]; then
        print_info "Starting in development mode with hot reload..."
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
    elif [ "$env" = "prod" ]; then
        print_info "Starting in production mode..."
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
    else
        print_info "Starting in default mode..."
        docker-compose up -d --build
    fi

    print_success "Services started."
}

# 等待MySQL就绪
wait_for_mysql() {
    print_info "Waiting for MySQL to be ready..."

    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -psmartmes123 &> /dev/null; then
            print_success "MySQL is ready!"
            return 0
        fi

        print_info "Attempt $attempt/$max_attempts: MySQL is not ready yet, waiting..."
        sleep 2
        attempt=$((attempt + 1))
    done

    print_error "MySQL failed to start within the expected time."
    return 1
}

# 检查服务健康状态
check_services_health() {
    print_info "Checking services health status..."

    # 等待几秒让服务启动
    sleep 5

    # 检查MySQL
    if docker-compose ps mysql | grep -q "Up"; then
        print_success "MySQL is running."
    else
        print_error "MySQL is not running."
    fi

    # 检查后端
    if docker-compose ps backend | grep -q "Up"; then
        print_success "Backend is running."
    else
        print_warning "Backend might still be starting..."
    fi

    # 检查前端
    if docker-compose ps frontend | grep -q "Up"; then
        print_success "Frontend is running."
    else
        print_warning "Frontend might still be starting..."
    fi
}

# 显示访问信息
show_access_info() {
    echo ""
    echo -e "${GREEN}=========================================="
    echo "  SmartMES Lite is now running!"
    echo "==========================================${NC}"
    echo ""
    echo -e "${BLUE}Access URLs:${NC}"
    echo "  Frontend: http://localhost"
    echo "  Backend API: http://localhost:8080/api"
    echo "  API Docs: http://localhost:8080/swagger-ui.html"
    echo ""
    echo -e "${BLUE}Default Login:${NC}"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
    echo -e "${BLUE}Database Connection:${NC}"
    echo "  Host: localhost"
    echo "  Port: 3306"
    echo "  Database: smartmes"
    echo "  Username: smartmes"
    echo "  Password: smartmes123"
    echo ""
    echo -e "${YELLOW}Useful Commands:${NC}"
    echo "  View logs: docker-compose logs -f [service]"
    echo "  Stop services: docker-compose down"
    echo "  Restart: docker-compose restart [service]"
    echo ""
}

# 显示使用帮助
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  dev       Start in development mode (with hot reload)"
    echo "  prod      Start in production mode (with resource limits)"
    echo "  clean     Stop and clean all containers and volumes"
    echo "  -h, --help Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0            # Start in default mode"
    echo "  $0 dev        # Start in development mode"
    echo "  $0 prod       # Start in production mode"
    echo "  $0 clean      # Clean all containers and volumes"
}

# 清理函数
clean_all() {
    print_warning "This will stop and remove all containers, networks, and volumes."
    read -p "Are you sure? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_info "Cleaning up..."
        docker-compose down -v --remove-orphans
        print_success "Cleanup completed."
    else
        print_info "Cleanup cancelled."
    fi
}

# 主函数
main() {
    print_banner

    # 处理参数
    case "${1:-}" in
        -h|--help)
            show_help
            exit 0
            ;;
        clean)
            clean_all
            exit 0
            ;;
        dev|prod)
            MODE=$1
            ;;
        "")
            MODE="default"
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac

    # 执行启动流程
    check_docker
    check_docker_compose
    create_directories
    setup_env
    cleanup
    start_services "$MODE"
    wait_for_mysql
    check_services_health
    show_access_info

    print_success "SmartMES Lite started successfully!"
}

# 执行主函数
main "$@"
