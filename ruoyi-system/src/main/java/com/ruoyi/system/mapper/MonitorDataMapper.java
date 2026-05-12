package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.monitor.MonitorData;

/**
 * 监控数据Mapper接口
 *
 * @author ruoyi
 */
public interface MonitorDataMapper {

    public MonitorData selectMonitorDataById(Long id);

    public List<MonitorData> selectMonitorDataList(MonitorData monitorData);

    public MonitorData selectLatestMonitorData(@Param("serverId") Long serverId);

    public List<MonitorData> selectMonitorDataRecent(@Param("serverId") Long serverId, @Param("limit") int limit);

    public int insertMonitorData(MonitorData monitorData);

    public int deleteMonitorDataById(Long id);

    public int deleteMonitorDataByIds(Long[] ids);

    public int deleteExpiredData();
}
