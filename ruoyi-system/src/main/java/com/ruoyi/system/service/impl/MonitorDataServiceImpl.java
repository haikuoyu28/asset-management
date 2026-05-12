package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.MonitorDataMapper;
import com.ruoyi.system.domain.monitor.MonitorData;
import com.ruoyi.system.service.IMonitorDataService;

/**
 * 监控数据Service实现
 *
 * @author ruoyi
 */
@Service
public class MonitorDataServiceImpl implements IMonitorDataService {

    @Autowired
    private MonitorDataMapper monitorDataMapper;

    @Override
    public MonitorData selectMonitorDataById(Long id) {
        return monitorDataMapper.selectMonitorDataById(id);
    }

    @Override
    public List<MonitorData> selectMonitorDataList(MonitorData monitorData) {
        return monitorDataMapper.selectMonitorDataList(monitorData);
    }

    @Override
    public MonitorData selectLatestMonitorData(Long serverId) {
        return monitorDataMapper.selectLatestMonitorData(serverId);
    }

    @Override
    public List<MonitorData> selectMonitorDataRecent(Long serverId, int limit) {
        return monitorDataMapper.selectMonitorDataRecent(serverId, limit);
    }

    @Override
    public int insertMonitorData(MonitorData monitorData) {
        return monitorDataMapper.insertMonitorData(monitorData);
    }

    @Override
    public int deleteMonitorDataById(Long id) {
        return monitorDataMapper.deleteMonitorDataById(id);
    }

    @Override
    public int deleteMonitorDataByIds(Long[] ids) {
        return monitorDataMapper.deleteMonitorDataByIds(ids);
    }

    @Override
    public int deleteExpiredData() {
        return monitorDataMapper.deleteExpiredData();
    }
}
