package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.monitor.MonitorAlarmRule;
import com.ruoyi.system.mapper.MonitorAlarmRuleMapper;
import com.ruoyi.system.service.IMonitorAlarmRuleService;

/**
 * Monitor alarm rule service implementation.
 */
@Service
public class MonitorAlarmRuleServiceImpl implements IMonitorAlarmRuleService {

    @Autowired
    private MonitorAlarmRuleMapper monitorAlarmRuleMapper;

    @Override
    public MonitorAlarmRule selectMonitorAlarmRuleById(Long id) {
        return monitorAlarmRuleMapper.selectMonitorAlarmRuleById(id);
    }

    @Override
    public List<MonitorAlarmRule> selectMonitorAlarmRuleList(MonitorAlarmRule monitorAlarmRule) {
        return monitorAlarmRuleMapper.selectMonitorAlarmRuleList(monitorAlarmRule);
    }

    @Override
    public List<MonitorAlarmRule> selectEnabledRules(Long serverId) {
        return monitorAlarmRuleMapper.selectEnabledRules(serverId);
    }

    @Override
    @Transactional
    public int insertMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule) {
        normalize(monitorAlarmRule);
        monitorAlarmRule.setCreateBy(SecurityUtils.getUsername());
        return monitorAlarmRuleMapper.insertMonitorAlarmRule(monitorAlarmRule);
    }

    @Override
    @Transactional
    public int updateMonitorAlarmRule(MonitorAlarmRule monitorAlarmRule) {
        normalize(monitorAlarmRule);
        monitorAlarmRule.setUpdateBy(SecurityUtils.getUsername());
        return monitorAlarmRuleMapper.updateMonitorAlarmRule(monitorAlarmRule);
    }

    @Override
    @Transactional
    public int deleteMonitorAlarmRuleById(Long id) {
        return monitorAlarmRuleMapper.deleteMonitorAlarmRuleById(id);
    }

    @Override
    @Transactional
    public int deleteMonitorAlarmRuleByIds(Long[] ids) {
        return monitorAlarmRuleMapper.deleteMonitorAlarmRuleByIds(ids);
    }

    private void normalize(MonitorAlarmRule rule) {
        if (rule.getCompareOperator() == null || rule.getCompareOperator().isEmpty()) {
            rule.setCompareOperator(">=");
        }
        if (rule.getScopeType() == null || rule.getScopeType().isEmpty()) {
            rule.setScopeType("0");
        }
        if (!"1".equals(rule.getScopeType())) {
            rule.setServerId(null);
        }
        if (rule.getSilentMinutes() == null || rule.getSilentMinutes() < 0) {
            rule.setSilentMinutes(30);
        }
        if (rule.getEnabled() == null || rule.getEnabled().isEmpty()) {
            rule.setEnabled("0");
        }
    }
}
