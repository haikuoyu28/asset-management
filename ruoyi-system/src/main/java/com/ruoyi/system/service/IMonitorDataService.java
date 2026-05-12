package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.monitor.MonitorData;

/**
 * 监控数据Service接口
 *
 * @author ruoyi
 */
public interface IMonitorDataService {

    public MonitorData selectMonitorDataById(Long id);

    public List<MonitorData> selectMonitorDataList(MonitorData monitorData);

    public MonitorData selectLatestMonitorData(Long serverId);

    public List<MonitorData> selectMonitorDataRecent(Long serverId, int limit);

    public int insertMonitorData(MonitorData monitorData);

    public int deleteMonitorDataById(Long id);

    public int deleteMonitorDataByIds(Long[] ids);

    public int deleteExpiredData();
}
