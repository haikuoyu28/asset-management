package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.monitor.MonitorAlarm;

/**
 * 监控告警Service接口
 *
 * @author ruoyi
 */
public interface IMonitorAlarmService {

    public MonitorAlarm selectMonitorAlarmById(Long id);

    public List<MonitorAlarm> selectMonitorAlarmList(MonitorAlarm monitorAlarm);

    public int selectUnhandleAlarmCount();

    public List<MonitorAlarm> selectUnhandleAlarmList();

    public int insertMonitorAlarm(MonitorAlarm monitorAlarm);

    public int updateMonitorAlarm(MonitorAlarm monitorAlarm);

    public int deleteMonitorAlarmById(Long id);

    public int deleteMonitorAlarmByIds(Long[] ids);

    public boolean checkAlarmExists(Long serverId, String alarmType);
}
