package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.monitor.MonitorAlarmRule;

/**
 * Monitor alarm rule service.
 */
public interface IMonitorAlarmRuleService {

    public MonitorAlarmRule selectMonitorAlarmRuleById(Long id);

    public List<MonitorAlarmRule> selectMonitorAlarmRuleList(MonitorAlarmRule monitorAlarmRule);

    public List<MonitorAlarmRule> selectEnabledRules(Long serverId);

    public int insertMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule);

    public int updateMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule);

    public int deleteMonitorAlarmRuleById(Long id);

    public int deleteMonitorAlarmRuleByIds(Long[] ids);
}
