package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.monitor.MonitorAlarmRule;

/**
 * Monitor alarm rule mapper.
 */
public interface MonitorAlarmRuleMapper {

    public MonitorAlarmRule selectMonitorAlarmRuleById(Long id);

    public List<MonitorAlarmRule> selectMonitorAlarmRuleList(MonitorAlarmRule monitorAlarmRule);

    public List<MonitorAlarmRule> selectEnabledRules(@Param("serverId") Long serverId);

    public int insertMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule);

    public int updateMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule);

    public int deleteMonitorAlarmRuleById(Long id);

    public int deleteMonitorAlarmRuleByIds(Long[] ids);
}
