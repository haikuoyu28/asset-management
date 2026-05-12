<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>欢迎使用企业IT资产与服务器监控平台</h1>
      <p>实时监控 · 智能告警 · 资产管理</p>
    </div>

    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-icon asset-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="9" y1="3" x2="9" y2="21"></line>
            <line x1="15" y1="3" x2="15" y2="21"></line>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ assetCount }}</div>
          <div class="stat-label">资产总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon server-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="2" width="20" height="8" rx="2" ry="2"></rect>
            <rect x="2" y="14" width="20" height="8" rx="2" ry="2"></rect>
            <line x1="6" y1="12" x2="6" y2="14"></line>
            <line x1="10" y1="12" x2="10" y2="14"></line>
            <line x1="14" y1="12" x2="14" y2="14"></line>
            <line x1="18" y1="12" x2="18" y2="14"></line>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ serverCount }}</div>
          <div class="stat-label">服务器数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon alarm-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value alarm-value">{{ alarmCount }}</div>
          <div class="stat-label">待处理告警</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon flow-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"></polyline>
            <polyline points="16 16 19.5 19.5 23 23"></polyline>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ flowCount }}</div>
          <div class="stat-label">资产变动</div>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="chart-section">
        <div class="section-header">
          <h2>服务器资源使用趋势</h2>
        </div>
        <div ref="chartRef" class="chart-container"></div>
      </div>

      <div class="quick-actions">
        <div class="section-header">
          <h2>快捷操作</h2>
        </div>
        <div class="action-grid">
          <button class="action-btn" @click="goToAsset">
            <div class="action-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="9" y1="3" x2="9" y2="21"></line>
              </svg>
            </div>
            <span>资产管理</span>
          </button>
          <button class="action-btn" @click="goToServer">
            <div class="action-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="2" width="20" height="8" rx="2" ry="2"></rect>
                <rect x="2" y="14" width="20" height="8" rx="2" ry="2"></rect>
              </svg>
            </div>
            <span>服务器监控</span>
          </button>
          <button class="action-btn" @click="goToAlarm">
            <div class="action-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              </svg>
            </div>
            <span>告警管理</span>
          </button>
          <button class="action-btn" @click="goToFlow">
            <div class="action-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"></polyline>
                <polyline points="16 16 19.5 19.5 23 23"></polyline>
              </svg>
            </div>
            <span>资产流程</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      assetCount: 128,
      serverCount: 24,
      alarmCount: 3,
      flowCount: 15,
      chartInstance: null
    }
  },
  mounted() {
    this.initChart()
  },
  beforeUnmount() {
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    initChart() {
      const chartDom = this.$refs.chartRef
      this.chartInstance = echarts.init(chartDom)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          data: ['CPU使用率', '内存使用率', '磁盘使用率'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: 'CPU使用率',
            type: 'line',
            smooth: true,
            data: [45, 52, 68, 75, 62, 58],
            lineStyle: {
              width: 3
            },
            areaStyle: {
              opacity: 0.1
            }
          },
          {
            name: '内存使用率',
            type: 'line',
            smooth: true,
            data: [62, 65, 70, 78, 72, 68],
            lineStyle: {
              width: 3
            },
            areaStyle: {
              opacity: 0.1
            }
          },
          {
            name: '磁盘使用率',
            type: 'line',
            smooth: true,
            data: [35, 38, 42, 45, 48, 50],
            lineStyle: {
              width: 3
            },
            areaStyle: {
              opacity: 0.1
            }
          }
        ]
      }
      
      this.chartInstance.setOption(option)
      
      window.addEventListener('resize', () => {
        this.chartInstance && this.chartInstance.resize()
      })
    },
    goToAsset() {
      this.$router.push('/asset/info')
    },
    goToServer() {
      this.$router.push('/monitor/server')
    },
    goToAlarm() {
      this.$router.push('/monitor/alarm')
    },
    goToFlow() {
      this.$router.push('/asset/flow')
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  min-height: 100vh;
  background: #f5f7fa;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 30px;
  
  h1 {
    font-size: 28px;
    color: #303133;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 14px;
    color: #909399;
  }
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  
  &.asset-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
  
  &.server-icon {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    color: #fff;
  }
  
  &.alarm-icon {
    background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
    color: #fff;
  }
  
  &.flow-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  
  &.alarm-value {
    color: #f56c6c;
  }
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-section, .quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 20px;
  
  h2 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.chart-container {
  height: 300px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: #e8eaed;
    transform: translateY(-2px);
  }
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 10px;
}

@media (max-width: 1200px) {
  .dashboard-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-stats {
    grid-template-columns: 1fr;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
}
</style>