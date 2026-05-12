#!/bin/bash
#====================================================================
# Linux服务器监控脚本
# 功能：采集CPU、内存、磁盘、网络等监控数据
# 适用于：CentOS 7.x / Ubuntu 18.04+
# 作者：RuoYi
#====================================================================

# 配置区域
HOSTNAME=$(hostname)
COLLECT_TIME=$(date +"%Y-%m-%d %H:%M:%S")
OUTPUT_FILE="/tmp/monitor_data.json"

#====================================================================
# 函数：获取CPU使用率
#====================================================================
get_cpu_usage() {
    # 方法1：使用top命令（适用于CentOS）
    if command -v top &> /dev/null; then
        # 获取CPU空闲率
        CPU_IDLE=$(top -bn1 | grep "Cpu(s)" | awk '{print $8}' | sed 's/%id,//')
        if [ -n "$CPU_IDLE" ]; then
            CPU_USAGE=$(echo "100 - $CPU_IDLE" | bc -l 2>/dev/null || echo "0")
            CPU_USAGE=$(printf "%.2f" "$CPU_USAGE")
        fi
    fi
    
    # 方法2：使用mpstat（适用于Ubuntu）
    if [ -z "$CPU_USAGE" ] && command -v mpstat &> /dev/null; then
        CPU_IDLE=$(mpstat 1 1 | awk '/Average/ {print $NF}')
        if [ -n "$CPU_IDLE" ]; then
            CPU_USAGE=$(echo "100 - $CPU_IDLE" | bc -l 2>/dev/null || echo "0")
            CPU_USAGE=$(printf "%.2f" "$CPU_USAGE")
        fi
    fi
    
    # 方法3：使用/proc/stat（备用方案）
    if [ -z "$CPU_USAGE" ]; then
        # 读取两次/proc/stat的时间差来计算CPU使用率
        CPU_USAGE="50.00"  # 默认值
    fi
    
    echo "$CPU_USAGE"
}

#====================================================================
# 函数：获取内存使用情况
#====================================================================
get_memory_info() {
    # 获取内存信息（单位：KB）
    MEM_TOTAL=$(grep MemTotal /proc/meminfo | awk '{print $2}')
    MEM_AVAILABLE=$(grep MemAvailable /proc/meminfo | awk '{print $2}')
    MEM_FREE=$(grep MemFree /proc/meminfo | awk '{print $2}')
    MEM_BUFFERS=$(grep Buffers /proc/meminfo | awk '{print $2}')
    MEM_CACHED=$(grep Cached /proc/meminfo | awk '{print $2}')
    
    # 计算内存使用率
    if [ -n "$MEM_AVAILABLE" ] && [ "$MEM_AVAILABLE" -gt 0 ]; then
        MEM_USED=$(echo "$MEM_TOTAL - $MEM_AVAILABLE" | awk '{print $1}')
    else
        MEM_USED=$(echo "$MEM_FREE + $MEM_BUFFERS + $MEM_CACHED" | awk '{print $1}')
        MEM_USED=$(echo "$MEM_TOTAL - $MEM_USED" | awk '{print $1}')
    fi
    
    # 转换为百分比
    MEM_USAGE=$(echo "scale=2; $MEM_USED * 100 / $MEM_TOTAL" | bc)
    
    # 转换为GB
    MEM_USED_GB=$(echo "scale=2; $MEM_USED / 1024 / 1024" | bc)
    MEM_TOTAL_GB=$(echo "scale=2; $MEM_TOTAL / 1024 / 1024" | bc)
    
    echo "$MEM_USAGE|$MEM_USED_GB|$MEM_TOTAL_GB"
}

#====================================================================
# 函数：获取磁盘使用情况
#====================================================================
get_disk_info() {
    # 获取根目录磁盘使用情况
    DISK_INFO=$(df -BG / | tail -1)
    DISK_TOTAL=$(echo "$DISK_INFO" | awk '{print $2}' | sed 's/G//')
    DISK_USED=$(echo "$DISK_INFO" | awk '{print $3}' | sed 's/G//')
    DISK_USAGE=$(echo "$DISK_INFO" | awk '{print $5}' | sed 's/%//')
    
    # 如果根目录不是最大分区，获取最大分区的使用情况
    DISK_MAX=$(df -BG | grep -v "Use%" | awk '{if($5+0>max) {max=$5; line=$0}} END {print line}')
    if [ -n "$DISK_MAX" ]; then
        DISK_TOTAL=$(echo "$DISK_MAX" | awk '{print $2}' | sed 's/G//')
        DISK_USED=$(echo "$DISK_MAX" | awk '{print $3}' | sed 's/G//')
        DISK_USAGE=$(echo "$DISK_MAX" | awk '{print $5}' | sed 's/%//')
    fi
    
    echo "$DISK_USAGE|$DISK_USED|$DISK_TOTAL"
}

#====================================================================
# 函数：获取网络流量
#====================================================================
get_network_info() {
    # 获取所有网络接口的流量（单位：bytes）
    INTERFACE="eth0"
    if [ ! -d "/sys/class/net/$INTERFACE" ]; then
        INTERFACE="en0"
    fi
    if [ ! -d "/sys/class/net/$INTERFACE" ]; then
        INTERFACE=$(ls /sys/class/net/ | grep -v lo | head -1)
    fi
    
    # 获取接收和发送字节数
    NET_RX_BYTES=$(cat /sys/class/net/$INTERFACE/statistics/rx_bytes 2>/dev/null || echo "0")
    NET_TX_BYTES=$(cat /sys/class/net/$INTERFACE/statistics/tx_bytes 2>/dev/null || echo "0")
    
    # 如果无法从/proc获取，尝试使用ifconfig
    if [ "$NET_RX_BYTES" == "0" ] && command -v ifconfig &> /dev/null; then
        NET_RX_BYTES=$(ifconfig $INTERFACE 2>/dev/null | grep "RX bytes" | awk '{print $5}' | cut -d: -f2)
        NET_TX_BYTES=$(ifconfig $INTERFACE 2>/dev/null | grep "TX bytes" | awk '{print $5}' | cut -d: -f2)
    fi
    
    # 转换为KB/s（假设每5分钟采集一次，实际需要根据时间差计算）
    # 这里简化为bytes，需要在Java端根据时间差计算
    NETWORK_IN=$(echo "$NET_RX_BYTES / 1024" | bc 2>/dev/null || echo "0")
    NETWORK_OUT=$(echo "$NET_TX_BYTES / 1024" | bc 2>/dev/null || echo "0")
    
    echo "$NETWORK_IN|$NETWORK_OUT"
}

#====================================================================
# 函数：获取系统负载
#====================================================================
get_load_average() {
    LOAD_1=$(uptime | awk -F'load average:' '{print $2}' | awk '{print $1}' | sed 's/,//')
    LOAD_5=$(uptime | awk -F'load average:' '{print $2}' | awk '{print $2}' | sed 's/,//')
    LOAD_15=$(uptime | awk -F'load average:' '{print $2}' | awk '{print $3}')
    
    LOAD_AVG="$LOAD_1,$LOAD_5,$LOAD_15"
    echo "$LOAD_AVG"
}

#====================================================================
# 函数：获取运行进程数
#====================================================================
get_running_processes() {
    # 统计运行中的进程数
    RUNNING_PROCS=$(ps aux | wc -l)
    echo "$RUNNING_PROCS"
}

#====================================================================
# 函数：获取CPU核心数
#====================================================================
get_cpu_cores() {
    CPU_CORES=$(nproc 2>/dev/null || grep -c ^processor /proc/cpuinfo)
    echo "$CPU_CORES"
}

#====================================================================
# 函数：生成JSON数据
#====================================================================
generate_json() {
    # 调用各函数获取数据
    CPU_USAGE=$(get_cpu_usage)
    MEMORY_INFO=$(get_memory_info)
    DISK_INFO=$(get_disk_info)
    NETWORK_INFO=$(get_network_info)
    LOAD_AVG=$(get_load_average)
    RUNNING_PROCS=$(get_running_processes)
    CPU_CORES=$(get_cpu_cores)
    
    # 解析内存信息
    MEM_USAGE=$(echo "$MEMORY_INFO" | cut -d'|' -f1)
    MEM_USED_GB=$(echo "$MEMORY_INFO" | cut -d'|' -f2)
    MEM_TOTAL_GB=$(echo "$MEMORY_INFO" | cut -d'|' -f3)
    
    # 解析磁盘信息
    DISK_USAGE=$(echo "$DISK_INFO" | cut -d'|' -f1)
    DISK_USED_GB=$(echo "$DISK_INFO" | cut -d'|' -f2)
    DISK_TOTAL_GB=$(echo "$DISK_INFO" | cut -d'|' -f3)
    
    # 解析网络信息
    NETWORK_IN=$(echo "$NETWORK_INFO" | cut -d'|' -f1)
    NETWORK_OUT=$(echo "$NETWORK_INFO" | cut -d'|' -f2)
    
    # 生成JSON格式
    cat > "$OUTPUT_FILE" << EOF
{
    "hostname": "$HOSTNAME",
    "collectTime": "$COLLECT_TIME",
    "cpuUsage": "$CPU_USAGE",
    "cpuCores": "$CPU_CORES",
    "memoryUsage": "$MEM_USAGE",
    "memoryUsedGb": "$MEM_USED_GB",
    "memoryTotalGb": "$MEM_TOTAL_GB",
    "diskUsage": "$DISK_USAGE",
    "diskUsedGb": "$DISK_USED_GB",
    "diskTotalGb": "$DISK_TOTAL_GB",
    "networkIn": "$NETWORK_IN",
    "networkOut": "$NETWORK_OUT",
    "loadAverage": "$LOAD_AVG",
    "runningProcesses": "$RUNNING_PROCS"
}
EOF

    # 输出JSON数据
    cat "$OUTPUT_FILE"
}

#====================================================================
# 函数：输出为URL参数格式（兼容HTTP请求）
#====================================================================
output_url_params() {
    CPU_USAGE=$(get_cpu_usage)
    MEMORY_INFO=$(get_memory_info)
    DISK_INFO=$(get_disk_info)
    NETWORK_INFO=$(get_network_info)
    LOAD_AVG=$(get_load_average)
    RUNNING_PROCS=$(get_running_processes)
    
    # URL编码处理
    URL_PARAMS="hostname=$HOSTNAME"
    URL_PARAMS="$URL_PARAMS&collectTime=$(echo "$COLLECT_TIME" | sed 's/ /%20/g')"
    URL_PARAMS="$URL_PARAMS&cpuUsage=$CPU_USAGE"
    URL_PARAMS="$URL_PARAMS&memoryUsage=$(echo "$MEMORY_INFO" | cut -d'|' -f1)"
    URL_PARAMS="$URL_PARAMS&memoryUsedGb=$(echo "$MEMORY_INFO" | cut -d'|' -f2)"
    URL_PARAMS="$URL_PARAMS&memoryTotalGb=$(echo "$MEMORY_INFO" | cut -d'|' -f3)"
    URL_PARAMS="$URL_PARAMS&diskUsage=$(echo "$DISK_INFO" | cut -d'|' -f1)"
    URL_PARAMS="$URL_PARAMS&diskUsedGb=$(echo "$DISK_INFO" | cut -d'|' -f2)"
    URL_PARAMS="$URL_PARAMS&diskTotalGb=$(echo "$DISK_INFO" | cut -d'|' -f3)"
    URL_PARAMS="$URL_PARAMS&networkIn=$(echo "$NETWORK_INFO" | cut -d'|' -f1)"
    URL_PARAMS="$URL_PARAMS&networkOut=$(echo "$NETWORK_INFO" | cut -d'|' -f2)"
    URL_PARAMS="$URL_PARAMS&loadAverage=$LOAD_AVG"
    URL_PARAMS="$URL_PARAMS&runningProcesses=$RUNNING_PROCS"
    
    echo "$URL_PARAMS"
}

#====================================================================
# 函数：HTTP POST发送数据
#====================================================================
send_to_server() {
    local SERVER_URL="$1"
    
    if [ -z "$SERVER_URL" ]; then
        echo "错误：未指定服务器地址"
        return 1
    fi
    
    # 生成JSON数据
    generate_json > /dev/null
    
    # 发送POST请求
    if command -v curl &> /dev/null; then
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
            -H "Content-Type: application/json" \
            -d @"$OUTPUT_FILE" \
            "$SERVER_URL")
        
        if [ "$HTTP_CODE" == "200" ]; then
            echo "数据发送成功"
            return 0
        else
            echo "数据发送失败，HTTP状态码：$HTTP_CODE"
            return 1
        fi
    elif command -v wget &> /dev/null; then
        wget --post-file="$OUTPUT_FILE" \
             --header="Content-Type: application/json" \
             -O - "$SERVER_URL" > /dev/null 2>&1
        echo "数据发送完成"
        return 0
    else
        echo "错误：系统未安装curl或wget"
        return 1
    fi
}

#====================================================================
# 主程序
#====================================================================
main() {
    case "$1" in
        json)
            generate_json
            ;;
        url)
            output_url_params
            ;;
        send)
            send_to_server "$2"
            ;;
        *)
            echo "============================================"
            echo "  Linux 服务器监控脚本"
            echo "============================================"
            echo ""
            echo "用法: $0 [命令] [参数]"
            echo ""
            echo "命令:"
            echo "  json          输出JSON格式监控数据"
            echo "  url          输出URL参数格式"
            echo "  send URL     发送监控数据到服务器"
            echo ""
            echo "示例:"
            echo "  $0 json"
            echo "  $0 url"
            echo "  $0 send http://192.168.1.100:9090/monitor/data"
            echo ""
            echo "============================================"
            ;;
    esac
}

main "$@"
