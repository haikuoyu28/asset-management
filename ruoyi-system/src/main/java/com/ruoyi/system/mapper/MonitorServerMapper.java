package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.monitor.MonitorServer;

/**
 * 监控服务器Mapper接口
 *
 * @author ruoyi
 */
public interface MonitorServerMapper {

    public MonitorServer selectMonitorServerById(Long id);

    public List<MonitorServer> selectMonitorServerList(MonitorServer monitorServer);

    public List<MonitorServer> selectMonitorServerOnlineList();

    public int insertMonitorServer(MonitorServer monitorServer);

    public int updateMonitorServer(MonitorServer monitorServer);

    public int updateCollectTime(Long serverId);

    public int updateCollectStatus(@Param("serverId") Long serverId, @Param("monitorStatus") String monitorStatus,
            @Param("connectionStatus") String connectionStatus, @Param("lastCollectTime") java.util.Date lastCollectTime);

    public int updateConnectionFailure(@Param("serverId") Long serverId);

    public int deleteMonitorServerById(Long id);

    public int deleteMonitorServerByIds(Long[] ids);

    public MonitorServer selectMonitorServerByIp(@Param("serverIp") String serverIp);

    public MonitorServer selectMonitorServerAuthById(Long id);

    public int checkServerIpUnique(@Param("serverIp") String serverIp, @Param("id") Long id);

}
