#!/bin/bash

echo "=================================="
echo "SmartMES Lite Frontend - 项目验证"
echo "=================================="
echo ""

# 检查Node.js版本
echo "1. 检查Node.js版本..."
node_version=$(node -v 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "   ✅ Node.js: $node_version"
else
    echo "   ❌ Node.js未安装"
    exit 1
fi

# 检查npm版本
echo "2. 检查npm版本..."
npm_version=$(npm -v 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "   ✅ npm: $npm_version"
else
    echo "   ❌ npm未安装"
    exit 1
fi

# 检查项目文件
echo ""
echo "3. 检查核心文件..."
files=(
    "package.json"
    "vite.config.js"
    "index.html"
    "src/App.vue"
    "src/main.js"
    "src/router/index.js"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

# 检查工单管理文件
echo ""
echo "4. 检查工单管理文件..."
workorder_files=(
    "src/views/WorkOrder/WorkOrderList.vue"
    "src/views/WorkOrder/CreateWorkOrder.vue"
    "src/views/WorkOrder/WorkOrderDetail.vue"
    "src/components/WorkOrderTable.vue"
)

for file in "${workorder_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

# 检查布局组件
echo ""
echo "5. 检查布局组件..."
layout_files=(
    "src/components/Layout/AppLayout.vue"
    "src/components/Layout/AppHeader.vue"
    "src/components/Layout/AppSidebar.vue"
)

for file in "${layout_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

# 检查API文件
echo ""
echo "6. 检查API文件..."
api_files=(
    "src/api/config.js"
    "src/api/request.js"
    "src/api/workorder.js"
)

for file in "${api_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

# 检查文档文件
echo ""
echo "7. 检查文档文件..."
doc_files=(
    "README.md"
    "SETUP.md"
    "PROJECT_OVERVIEW.md"
    "WORK_ORDER_DELIVERY.md"
)

for file in "${doc_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

# 统计代码行数
echo ""
echo "8. 统计代码行数..."
vue_files=$(find src -name "*.vue" | wc -l | tr -d ' ')
js_files=$(find src -name "*.js" | wc -l | tr -d ' ')
echo "   Vue组件: $vue_files 个"
echo "   JS文件: $js_files 个"

# 检查node_modules
echo ""
echo "9. 检查依赖安装..."
if [ -d "node_modules" ]; then
    echo "   ✅ node_modules已安装"
else
    echo "   ⚠️  node_modules未安装，请运行: npm install"
fi

echo ""
echo "=================================="
echo "验证完成！"
echo "=================================="
echo ""
echo "下一步操作："
echo "1. 如果依赖未安装，运行: npm install"
echo "2. 启动开发服务器: npm run dev"
echo "3. 访问: http://localhost:5173"
echo ""
