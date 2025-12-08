#!/bin/bash

###############################################################################
# SmartMES Lite 停止脚本
# 用途：优雅地停止所有Docker服务
# 使用：./stop.sh [OPTIONS]
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
    echo "  SmartMES Lite - 停止服务脚本"
    echo "=========================================="
    echo -e "${NC}"
}

# 显示使用帮助
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -v, --volumes  Stop and remove volumes (删除数据)"
    echo "  -h, --help     Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                # 停止服务（保留数据）"
    echo "  $0 --volumes      # 停止服务并删除所有数据"
}

# 停止服务
stop_services() {
    local remove_volumes=$1

    if [ "$remove_volumes" = true ]; then
        print_warning "This will stop all services and REMOVE ALL DATA!"
        read -p "Are you sure? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "Operation cancelled."
            exit 0
        fi

        print_info "Stopping services and removing volumes..."
        docker-compose down -v --remove-orphans
        print_success "Services stopped and data removed."
    else
        print_info "Stopping services (data will be preserved)..."
        docker-compose down --remove-orphans
        print_success "Services stopped. Data has been preserved."
    fi
}

# 验证停止状态
verify_stopped() {
    print_info "Verifying all services are stopped..."

    local running_containers=$(docker-compose ps -q 2>/dev/null | wc -l)

    if [ "$running_containers" -eq 0 ]; then
        print_success "All services are stopped."
        return 0
    else
        print_warning "Some containers may still be running."
        docker-compose ps
        return 1
    fi
}

# 显示状态信息
show_status() {
    echo ""
    echo -e "${GREEN}=========================================="
    echo "  Service Status"
    echo "==========================================${NC}"
    echo ""

    local mysql_running=$(docker ps -q -f name=smartmes-mysql 2>/dev/null | wc -l)
    local backend_running=$(docker ps -q -f name=smartmes-backend 2>/dev/null | wc -l)
    local frontend_running=$(docker ps -q -f name=smartmes-frontend 2>/dev/null | wc -l)

    if [ "$mysql_running" -eq 0 ]; then
        echo -e "${GREEN}✓${NC} MySQL is stopped"
    else
        echo -e "${RED}✗${NC} MySQL is still running"
    fi

    if [ "$backend_running" -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Backend is stopped"
    else
        echo -e "${RED}✗${NC} Backend is still running"
    fi

    if [ "$frontend_running" -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Frontend is stopped"
    else
        echo -e "${RED}✗${NC} Frontend is still running"
    fi

    echo ""
}

# 显示数据卷信息
show_volumes_info() {
    echo -e "${BLUE}Data Volumes:${NC}"

    local volumes=$(docker volume ls -q -f name=test_demo 2>/dev/null)

    if [ -z "$volumes" ]; then
        echo "  No data volumes found."
    else
        echo "$volumes" | while read volume; do
            local size=$(docker volume inspect "$volume" --format '{{ .Mountpoint }}' | xargs du -sh 2>/dev/null | cut -f1)
            echo "  - $volume ($size)"
        done
    fi

    echo ""
}

# 主函数
main() {
    print_banner

    local remove_volumes=false

    # 处理参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -v|--volumes)
                remove_volumes=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done

    # 停止服务
    stop_services "$remove_volumes"

    # 验证停止状态
    verify_stopped

    # 显示状态
    show_status

    # 显示数据卷信息
    if [ "$remove_volumes" = false ]; then
        show_volumes_info
    fi

    echo -e "${BLUE}Useful Commands:${NC}"
    echo "  Start services again: ./start.sh"
    echo "  View remaining volumes: docker volume ls -f name=test_demo"
    echo "  Remove volumes manually: docker-compose down -v"
    echo ""

    print_success "SmartMES Lite stopped successfully!"
}

# 执行主函数
main "$@"
