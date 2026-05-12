package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.monitor.MonitorAlarm;

/**
 * 监控告警Mapper接口
 *
 * @author ruoyi
 */
public interface MonitorAlarmMapper {

    public MonitorAlarm selectMonitorAlarmById(Long id);

    public List<MonitorAlarm> selectMonitorAlarmList(MonitorAlarm monitorAlarm);

    public int selectUnhandleAlarmCount();

    public List<MonitorAlarm> selectUnhandleAlarmList();

    public int insertMonitorAlarm(MonitorAlarm monitorAlarm);

    public int updateMonitorAlarm(MonitorAlarm monitorAlarm);

    public int deleteMonitorAlarmById(Long id);

    public int deleteMonitorAlarmByIds(Long[] ids);

    public int checkAlarmExists(@Param("serverId") Long serverId, @Param("alarmType") String alarmType);
}
