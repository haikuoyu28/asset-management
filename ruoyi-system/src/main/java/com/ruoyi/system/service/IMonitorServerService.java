package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.monitor.MonitorServer;
import com.ruoyi.system.domain.monitor.MonitorData;

/**
 * 监控服务器Service接口
 *
 * @author ruoyi
 */
public interface IMonitorServerService {

    public MonitorServer selectMonitorServerById(Long id);

    public List<MonitorServer> selectMonitorServerList(MonitorServer monitorServer);

    public List<MonitorServer> selectMonitorServerOnlineList();

    public int insertMonitorServer(MonitorServer monitorServer);

    public int updateMonitorServer(MonitorServer monitorServer);

    public int updateCollectTime(Long serverId);

    public void testSshConnection(Long id);

    public MonitorData collectSshMonitorData(Long id);

    public int collectAllSshMonitorData();

    public Map<String, Object> executePresetCommand(Long id, String commandKey);

    public int deleteMonitorServerById(Long id);

    public int deleteMonitorServerByIds(Long[] ids);

    public boolean checkServerIpUnique(MonitorServer monitorServer);
}
