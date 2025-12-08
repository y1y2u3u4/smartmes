#!/bin/bash

###############################################################################
# SmartMES Lite 数据库初始化脚本
# 用途：等待MySQL就绪并执行数据库初始化脚本
# 使用：./init-db.sh
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

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 数据库配置（从环境变量获取，或使用默认值）
DB_HOST="${DB_HOST:-mysql}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${MYSQL_DATABASE:-smartmes}"
DB_USER="${MYSQL_USER:-smartmes}"
DB_PASSWORD="${MYSQL_PASSWORD:-smartmes123}"
DB_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-smartmes123}"

# 脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 等待MySQL就绪
wait_for_mysql() {
    print_info "Waiting for MySQL to be ready..."

    local max_attempts=60
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p"${DB_ROOT_PASSWORD}" &> /dev/null; then
            print_success "MySQL is ready!"
            return 0
        fi

        if [ $((attempt % 10)) -eq 0 ]; then
            print_info "Still waiting for MySQL... (attempt $attempt/$max_attempts)"
        fi

        sleep 2
        attempt=$((attempt + 1))
    done

    print_error "MySQL failed to start within the expected time."
    return 1
}

# 检查数据库是否已初始化
check_database_initialized() {
    print_info "Checking if database is already initialized..."

    local table_count=$(docker-compose exec -T mysql mysql -h localhost -u"${DB_USER}" -p"${DB_PASSWORD}" -D"${DB_NAME}" -e "SELECT COUNT(*) as count FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" -s -N 2>/dev/null || echo "0")

    if [ "$table_count" -gt 0 ]; then
        print_info "Database already has $table_count tables. Skipping initialization."
        return 1
    else
        print_info "Database is empty. Will proceed with initialization."
        return 0
    fi
}

# 执行SQL文件
execute_sql_file() {
    local sql_file=$1
    local file_name=$(basename "$sql_file")

    if [ ! -f "$sql_file" ]; then
        print_error "SQL file not found: $sql_file"
        return 1
    fi

    print_info "Executing SQL file: $file_name"

    # 执行SQL文件
    if docker-compose exec -T mysql mysql -h localhost -u"${DB_USER}" -p"${DB_PASSWORD}" -D"${DB_NAME}" < "$sql_file"; then
        print_success "Successfully executed: $file_name"
        return 0
    else
        print_error "Failed to execute: $file_name"
        return 1
    fi
}

# 初始化数据库
initialize_database() {
    print_info "Starting database initialization..."

    # 检查是否需要初始化
    if ! check_database_initialized; then
        print_info "Database is already initialized. Exiting."
        return 0
    fi

    # 执行schema.sql（表结构）
    if [ -f "${SCRIPT_DIR}/schema.sql" ]; then
        execute_sql_file "${SCRIPT_DIR}/schema.sql"
    else
        print_error "schema.sql not found in ${SCRIPT_DIR}"
        exit 1
    fi

    # 执行init_data.sql（初始数据）
    if [ -f "${SCRIPT_DIR}/init_data.sql" ]; then
        execute_sql_file "${SCRIPT_DIR}/init_data.sql"
    else
        print_error "init_data.sql not found in ${SCRIPT_DIR}"
        exit 1
    fi

    print_success "Database initialization completed!"
}

# 验证初始化结果
verify_initialization() {
    print_info "Verifying database initialization..."

    # 检查表数量
    local table_count=$(docker-compose exec -T mysql mysql -h localhost -u"${DB_USER}" -p"${DB_PASSWORD}" -D"${DB_NAME}" -e "SELECT COUNT(*) as count FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" -s -N)

    print_info "Total tables created: $table_count"

    # 检查用户数量
    local user_count=$(docker-compose exec -T mysql mysql -h localhost -u"${DB_USER}" -p"${DB_PASSWORD}" -D"${DB_NAME}" -e "SELECT COUNT(*) FROM users;" -s -N 2>/dev/null || echo "0")

    print_info "Total users in database: $user_count"

    if [ "$table_count" -gt 0 ] && [ "$user_count" -gt 0 ]; then
        print_success "Database verification passed!"
        return 0
    else
        print_error "Database verification failed!"
        return 1
    fi
}

# 显示数据库信息
show_database_info() {
    echo ""
    echo -e "${GREEN}=========================================="
    echo "  Database Information"
    echo "==========================================${NC}"
    echo ""
    echo -e "${BLUE}Connection Details:${NC}"
    echo "  Host: ${DB_HOST}"
    echo "  Port: ${DB_PORT}"
    echo "  Database: ${DB_NAME}"
    echo "  Username: ${DB_USER}"
    echo "  Password: ${DB_PASSWORD}"
    echo ""
    echo -e "${BLUE}Default Admin User:${NC}"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
}

# 主函数
main() {
    echo -e "${BLUE}"
    echo "=========================================="
    echo "  SmartMES Lite - Database Initialization"
    echo "=========================================="
    echo -e "${NC}"

    # 等待MySQL就绪
    if ! wait_for_mysql; then
        exit 1
    fi

    # 初始化数据库
    initialize_database

    # 验证初始化结果
    if verify_initialization; then
        show_database_info
        print_success "Database initialization script completed successfully!"
    else
        print_error "Database initialization script failed!"
        exit 1
    fi
}

# 执行主函数
main "$@"
