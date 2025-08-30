#!/bin/bash

# 研发工单系统启动脚本
# 使用说明：chmod +x start.sh && ./start.sh

echo "=========================================="
echo "    研发工单系统 (SY-Ticket) 启动脚本"
echo "=========================================="
echo ""

# 检查Java版本
echo "🔍 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到Java环境，请安装Java 17或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f 2 | cut -d'.' -f 1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ 错误：Java版本过低，需要Java 17或更高版本，当前版本：$JAVA_VERSION"
    exit 1
fi
echo "✅ Java环境检查通过"

# 检查Maven
echo "🔍 检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：未找到Maven，请安装Maven 3.6或更高版本"
    exit 1
fi
echo "✅ Maven环境检查通过"

# 检查配置文件
echo "🔍 检查配置文件..."
if [ ! -f "src/main/resources/application.yml" ]; then
    echo "❌ 错误：未找到配置文件 application.yml"
    exit 1
fi
echo "✅ 配置文件检查通过"

echo ""
echo "📋 启动前请确认以下服务已启动："
echo "   1. MySQL数据库服务"
echo "   2. MinIO对象存储服务"
echo ""
echo "📝 数据库配置（application.yml）："
echo "   - 数据库：sy_ticket"
echo "   - 用户名/密码：请检查配置文件"
echo ""
echo "🔧 MinIO配置（application.yml）："
echo "   - 地址：http://localhost:9000"
echo "   - Access Key/Secret Key：请检查配置文件"
echo ""

read -p "确认环境配置无误，按回车键继续启动..."

echo ""
echo "🚀 正在启动应用..."
echo "----------------------------------------"

# 启动应用
mvn spring-boot:run

echo ""
echo "=========================================="
echo "启动完成！"
echo ""
echo "🌐 访问地址：http://localhost:8080"
echo "👤 默认账户：admin / admin123"
echo ""
echo "📊 MinIO控制台：http://localhost:9001"
echo "🛠️ 停止应用：Ctrl + C"
echo "=========================================="