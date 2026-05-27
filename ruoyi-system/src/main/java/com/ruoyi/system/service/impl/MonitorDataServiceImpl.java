package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.monitor.MonitorAlarm;
import com.ruoyi.system.domain.monitor.MonitorAlarmRule;
import com.ruoyi.system.mapper.MonitorDataMapper;
import com.ruoyi.system.mapper.MonitorAlarmMapper;
import com.ruoyi.system.mapper.MonitorAlarmRuleMapper;
import com.ruoyi.system.mapper.MonitorServerMapper;
import com.ruoyi.system.domain.monitor.MonitorData;
import com.ruoyi.system.domain.monitor.MonitorServer;
import com.ruoyi.system.service.IMonitorDataService;

/**
 * 监控数据Service实现
 *
 * @author ruoyi
 */
@Service
public class MonitorDataServiceImpl implements IMonitorDataService {

    @Autowired
    private MonitorDataMapper monitorDataMapper;

    @Autowired
    private MonitorServerMapper monitorServerMapper;

    @Autowired
    private MonitorAlarmRuleMapper monitorAlarmRuleMapper;

    @Autowired
    private MonitorAlarmMapper monitorAlarmMapper;

    @Override
    public MonitorData selectMonitorDataById(Long id) {
        return monitorDataMapper.selectMonitorDataById(id);
    }

    @Override
    public List<MonitorData> selectMonitorDataList(MonitorData monitorData) {
        return monitorDataMapper.selectMonitorDataList(monitorData);
    }

    @Override
    public MonitorData selectLatestMonitorData(Long serverId) {
        return monitorDataMapper.selectLatestMonitorData(serverId);
    }

    @Override
    public List<MonitorData> selectMonitorDataRecent(Long serverId, int limit) {
        return monitorDataMapper.selectMonitorDataRecent(serverId, limit);
    }

    @Override
    public int insertMonitorData(MonitorData monitorData) {
        return monitorDataMapper.insertMonitorData(monitorData);
    }

    @Override
    @Transactional
    public MonitorData reportMonitorData(MonitorData monitorData) {
        MonitorServer server = resolveServer(monitorData);
        monitorData.setServerId(server.getId());
        monitorData.setServerIp(server.getServerIp());
        if (monitorData.getCollectTime() == null) {
            monitorData.setCollectTime(new Date());
        }

        monitorDataMapper.insertMonitorData(monitorData);

        boolean abnormal = evaluateRules(server, monitorData);
        monitorServerMapper.updateCollectStatus(server.getId(), abnormal ? "1" : "0", "0", monitorData.getCollectTime());
        return monitorData;
    }

    @Override
    public int deleteMonitorDataById(Long id) {
        return monitorDataMapper.deleteMonitorDataById(id);
    }

    @Override
    public int deleteMonitorDataByIds(Long[] ids) {
        return monitorDataMapper.deleteMonitorDataByIds(ids);
    }

    @Override
    public int deleteExpiredData() {
        return monitorDataMapper.deleteExpiredData();
    }

    private MonitorServer resolveServer(MonitorData monitorData) {
        MonitorServer server = null;
        if (monitorData.getServerId() != null) {
            server = monitorServerMapper.selectMonitorServerById(monitorData.getServerId());
        }
        if (server == null && StringUtils.isNotEmpty(monitorData.getServerIp())) {
            server = monitorServerMapper.selectMonitorServerByIp(monitorData.getServerIp());
        }
        if (server == null) {
            throw new ServiceException("未找到对应的监控服务器，请先在服务器管理中纳管该服务器");
        }
        return server;
    }

    private boolean evaluateRules(MonitorServer server, MonitorData data) {
        boolean abnormal = false;
        List<MonitorAlarmRule> rules = monitorAlarmRuleMapper.selectEnabledRules(server.getId());
        for (MonitorAlarmRule rule : rules) {
            BigDecimal value = metricValue(rule.getAlarmType(), data);
            if (value == null || rule.getThresholdValue() == null) {
                continue;
            }
            if (matches(rule.getCompareOperator(), value, rule.getThresholdValue())) {
                abnormal = true;
                createAlarmIfNeeded(server, data, rule, value);
            }
        }
        return abnormal;
    }

    private BigDecimal metricValue(String alarmType, MonitorData data) {
        if ("1".equals(alarmType)) {
            return data.getCpuUsage();
        }
        if ("2".equals(alarmType)) {
            return data.getMemoryUsage();
        }
        if ("3".equals(alarmType)) {
            return data.getDiskUsage();
        }
        return null;
    }

    private boolean matches(String operator, BigDecimal value, BigDecimal threshold) {
        int compare = value.compareTo(threshold);
        if (">".equals(operator)) {
            return compare > 0;
        }
        if (">=".equals(operator) || StringUtils.isEmpty(operator)) {
            return compare >= 0;
        }
        if ("<".equals(operator)) {
            return compare < 0;
        }
        if ("<=".equals(operator)) {
            return compare <= 0;
        }
        return compare >= 0;
    }

    private void createAlarmIfNeeded(MonitorServer server, MonitorData data, MonitorAlarmRule rule, BigDecimal value) {
        if (monitorAlarmMapper.checkAlarmExists(server.getId(), rule.getAlarmType()) > 0) {
            return;
        }
        MonitorAlarm alarm = new MonitorAlarm();
        alarm.setServerId(server.getId());
        alarm.setServerIp(server.getServerIp());
        alarm.setHostname(server.getHostname());
        alarm.setAlarmType(rule.getAlarmType());
        alarm.setAlarmLevel(rule.getAlarmLevel());
        alarm.setThresholdValue(rule.getThresholdValue());
        alarm.setAlarmValue(value);
        alarm.setAlarmStatus("0");
        alarm.setAlarmMessage(buildAlarmMessage(server, rule, value));
        alarm.setAlarmTime(data.getCollectTime());
        alarm.setCreateBy("system");
        monitorAlarmMapper.insertMonitorAlarm(alarm);
    }

    private String buildAlarmMessage(MonitorServer server, MonitorAlarmRule rule, BigDecimal value) {
        return String.format("%s %s%s%%，当前值 %s%%，服务器 %s",
                metricName(rule.getAlarmType()),
                rule.getCompareOperator(),
                rule.getThresholdValue(),
                value,
                StringUtils.isNotEmpty(server.getHostname()) ? server.getHostname() : server.getServerIp());
    }

    private String metricName(String alarmType) {
        if ("1".equals(alarmType)) {
            return "CPU使用率";
        }
        if ("2".equals(alarmType)) {
            return "内存使用率";
        }
        if ("3".equals(alarmType)) {
            return "磁盘使用率";
        }
        return "监控指标";
    }
}
