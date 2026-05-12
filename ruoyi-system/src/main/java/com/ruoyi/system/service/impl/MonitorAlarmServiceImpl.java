package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.MonitorAlarmMapper;
import com.ruoyi.system.domain.monitor.MonitorAlarm;
import com.ruoyi.system.service.IMonitorAlarmService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 监控告警Service实现
 *
 * @author ruoyi
 */
@Service
public class MonitorAlarmServiceImpl implements IMonitorAlarmService {

    @Autowired
    private MonitorAlarmMapper monitorAlarmMapper;

    @Override
    public MonitorAlarm selectMonitorAlarmById(Long id) {
        return monitorAlarmMapper.selectMonitorAlarmById(id);
    }

    @Override
    public List<MonitorAlarm> selectMonitorAlarmList(MonitorAlarm monitorAlarm) {
        return monitorAlarmMapper.selectMonitorAlarmList(monitorAlarm);
    }

    @Override
    public int selectUnhandleAlarmCount() {
        return monitorAlarmMapper.selectUnhandleAlarmCount();
    }

    @Override
    public List<MonitorAlarm> selectUnhandleAlarmList() {
        return monitorAlarmMapper.selectUnhandleAlarmList();
    }

    @Override
    @Transactional
    public int insertMonitorAlarm(MonitorAlarm monitorAlarm) {
        monitorAlarm.setCreateBy(SecurityUtils.getUsername());
        return monitorAlarmMapper.insertMonitorAlarm(monitorAlarm);
    }

    @Override
    @Transactional
    public int updateMonitorAlarm(MonitorAlarm monitorAlarm) {
        monitorAlarm.setUpdateBy(SecurityUtils.getUsername());
        return monitorAlarmMapper.updateMonitorAlarm(monitorAlarm);
    }

    @Override
    @Transactional
    public int deleteMonitorAlarmById(Long id) {
        return monitorAlarmMapper.deleteMonitorAlarmById(id);
    }

    @Override
    @Transactional
    public int deleteMonitorAlarmByIds(Long[] ids) {
        return monitorAlarmMapper.deleteMonitorAlarmByIds(ids);
    }

    @Override
    public boolean checkAlarmExists(Long serverId, String alarmType) {
        return monitorAlarmMapper.checkAlarmExists(serverId, alarmType) > 0;
    }
}
