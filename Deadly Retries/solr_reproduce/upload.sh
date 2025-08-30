#!/bin/bash

# ==================== 配置变量 ====================
# 服务器配置
USER="ZhenyuLi"
DOMAIN="utah.cloudlab.us"
SERVERS=(
  "ms1132"
  "ms1101"
)

# 文件路径 - 监控工具相关
SOLR_INST_JAR="/Users/lizhenyu/Desktop/baseline/vcstudy/Deadly Retries/hbase-instrumentation-master/solr-instrumentation-1.0-SNAPSHOT.jar"
INST_LIBS_JAR="/Users/lizhenyu/Desktop/baseline/vcstudy/Deadly Retries/hbase-instrumentation-master/instrument-libs/target/instrument-libs-1.0-SNAPSHOT.jar"
ASPECTJ_JAR="/Users/lizhenyu/IdeaProjects/vcstudy/deadlyretries/hbase-instrumentation-master/aspectjweaver-1.9.7.jar"

# 远程目标目录
REMOTE_LIB_DIR="/opt/Solr/solr/server/solr-webapp/webapp/WEB-INF/lib"
REMOTE_EXT_DIR="/opt/Solr/solr/server/lib/ext"

# SSH 选项
SSH_OPTIONS="-o StrictHostKeyChecking=no -o ConnectTimeout=10"

# ==================== 主逻辑 ====================

echo "========================================="
echo "开始部署监控工具到 ${#SERVERS[@]} 个服务器"
echo "========================================="

# 检查本地文件是否存在
for FILE in "$SOLR_INST_JAR" "$INST_LIBS_JAR" "$ASPECTJ_JAR"; do
    if [ ! -f "$FILE" ]; then
        echo "错误: 文件 $FILE 不存在！"
        exit 1
    fi
done

# 用于记录失败的服务器
FAILED_SERVERS=()

# 遍历服务器列表
for SERVER in "${SERVERS[@]}"; do
    FULL_HOST="${USER}@${SERVER}.${DOMAIN}"
    echo ""
    echo ">>> 处理服务器: ${SERVER}"
    echo "----------------------------------------"


    # 2. 上传 solr-instrumentation JAR
    echo "  上传 solr-instrumentation JAR..."
    scp $SSH_OPTIONS "$SOLR_INST_JAR" "${FULL_HOST}:${REMOTE_LIB_DIR}/"
    if [ $? -ne 0 ]; then
        echo "  ✗ solr-instrumentation JAR 上传失败"
        FAILED_SERVERS+=("$SERVER")
        continue
    fi
    echo "  ✓ solr-instrumentation JAR 上传成功"

    # 3. 上传 instrument-libs JAR
    echo "  上传 instrument-libs JAR..."
    scp $SSH_OPTIONS "$INST_LIBS_JAR" "${FULL_HOST}:${REMOTE_LIB_DIR}/"
    if [ $? -ne 0 ]; then
        echo "  ✗ instrument-libs JAR 上传失败"
        FAILED_SERVERS+=("$SERVER")
        continue
    fi
    echo "  ✓ instrument-libs JAR 上传成功"

    # 4. 上传 AspectJ weaver JAR
    echo "  [4/5] 上传 AspectJ weaver JAR..."
    scp $SSH_OPTIONS "$ASPECTJ_JAR" "${FULL_HOST}:${REMOTE_EXT_DIR}/"
    if [ $? -ne 0 ]; then
        echo "  ✗ AspectJ weaver JAR 上传失败"
        FAILED_SERVERS+=("$SERVER")
        continue
    fi
    echo "  ✓ AspectJ weaver JAR 上传成功"

#    # 5. 设置文件权限
#    echo "  [5/5] 设置文件权限..."
#    ssh $SSH_OPTIONS "$FULL_HOST" "chmod 644 ${REMOTE_LIB_DIR}/solr-instrumentation-1.0-SNAPSHOT.jar ${REMOTE_LIB_DIR}/instrument-libs-1.0-SNAPSHOT.jar ${REMOTE_EXT_DIR}/aspectjweaver-1.9.7.jar"
#    if [ $? -ne 0 ]; then
#        echo "  ✗ 设置权限失败"
#        FAILED_SERVERS+=("$SERVER")
#        continue
#    fi
#    echo "  ✓ 权限设置成功"
#
#    echo "  ✓ 服务器 ${SERVER} 处理完成"
done

echo ""
echo "========================================="
echo "部署完成汇总"
echo "========================================="
echo "成功: $((${#SERVERS[@]} - ${#FAILED_SERVERS[@]})) 个服务器"
echo "失败: ${#FAILED_SERVERS[@]} 个服务器"

if [ ${#FAILED_SERVERS[@]} -gt 0 ]; then
    echo ""
    echo "失败的服务器列表:"
    for FAILED in "${FAILED_SERVERS[@]}"; do
        echo "  - $FAILED"
    done
    exit 1
fi

exit 0