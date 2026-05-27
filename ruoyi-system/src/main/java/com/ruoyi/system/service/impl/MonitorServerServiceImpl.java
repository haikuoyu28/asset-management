package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.monitor.MonitorAlarm;
import com.ruoyi.system.mapper.MonitorAlarmMapper;
import com.ruoyi.system.mapper.MonitorServerMapper;
import com.ruoyi.system.domain.monitor.MonitorServer;
import com.ruoyi.system.service.IMonitorServerService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 监控服务器Service实现
 *
 * @author ruoyi
 */
@Service
public class MonitorServerServiceImpl implements IMonitorServerService {

    @Autowired
    private MonitorServerMapper monitorServerMapper;

    @Autowired
    private MonitorAlarmMapper monitorAlarmMapper;

    @Override
    public MonitorServer selectMonitorServerById(Long id) {
        return monitorServerMapper.selectMonitorServerById(id);
    }

    @Override
    public List<MonitorServer> selectMonitorServerList(MonitorServer monitorServer) {
        return monitorServerMapper.selectMonitorServerList(monitorServer);
    }

    @Override
    public List<MonitorServer> selectMonitorServerOnlineList() {
        return monitorServerMapper.selectMonitorServerOnlineList();
    }

    @Override
    @Transactional
    public int insertMonitorServer(MonitorServer monitorServer) {
        if (!checkServerIpUnique(monitorServer)) {
            throw new ServiceException("服务器IP已存在");
        }
        monitorServer.setSshPassword(null);
        if (StringUtils.isEmpty(monitorServer.getAgentEnabled())) {
            monitorServer.setAgentEnabled("1");
        }
        monitorServer.setCreateBy(SecurityUtils.getUsername());
        return monitorServerMapper.insertMonitorServer(monitorServer);
    }

    @Override
    @Transactional
    public int updateMonitorServer(MonitorServer monitorServer) {
        if (!checkServerIpUnique(monitorServer)) {
            throw new ServiceException("服务器IP已存在");
        }
        monitorServer.setSshPassword(null);
        monitorServer.setAgentToken(null);
        monitorServer.setUpdateBy(SecurityUtils.getUsername());
        return monitorServerMapper.updateMonitorServer(monitorServer);
    }

    @Override
    public int updateCollectTime(Long serverId) {
        return monitorServerMapper.updateCollectTime(serverId);
    }

    @Override
    @Transactional
    public MonitorServer resetAgentToken(Long id) {
        MonitorServer server = monitorServerMapper.selectMonitorServerById(id);
        if (server == null) {
            throw new ServiceException("服务器不存在");
        }
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        MonitorServer update = new MonitorServer();
        update.setId(id);
        update.setAgentToken(token);
        update.setAgentEnabled("0");
        update.setUpdateBy(SecurityUtils.getUsername());
        monitorServerMapper.updateAgentToken(update);
        server.setAgentToken(token);
        server.setAgentEnabled("0");
        return server;
    }

    @Override
    @Transactional
    public int checkAgentHeartbeatTimeout(int timeoutMinutes) {
        int timeout = timeoutMinutes <= 0 ? 3 : timeoutMinutes;
        List<MonitorServer> servers = monitorServerMapper.selectAgentHeartbeatTimeoutList(timeout);
        for (MonitorServer server : servers) {
            monitorServerMapper.updateAgentOffline(server.getId());
            createOfflineAlarmIfNeeded(server, timeout);
        }
        return servers.size();
    }

    @Override
    @Transactional
    public int deleteMonitorServerById(Long id) {
        return monitorServerMapper.deleteMonitorServerById(id);
    }

    @Override
    @Transactional
    public int deleteMonitorServerByIds(Long[] ids) {
        return monitorServerMapper.deleteMonitorServerByIds(ids);
    }

    @Override
    public boolean checkServerIpUnique(MonitorServer monitorServer) {
        Long id = StringUtils.isNull(monitorServer.getId()) ? -1L : monitorServer.getId();
        MonitorServer info = monitorServerMapper.selectMonitorServerByIp(monitorServer.getServerIp());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue()) {
            return false;
        }
        return true;
    }

    private void createOfflineAlarmIfNeeded(MonitorServer server, int timeoutMinutes) {
        if (monitorAlarmMapper.checkAlarmExists(server.getId(), "5") > 0) {
            return;
        }
        MonitorAlarm alarm = new MonitorAlarm();
        alarm.setServerId(server.getId());
        alarm.setServerIp(server.getServerIp());
        alarm.setHostname(server.getHostname());
        alarm.setAlarmType("5");
        alarm.setAlarmLevel("3");
        alarm.setThresholdValue(new BigDecimal(timeoutMinutes));
        alarm.setAlarmValue(new BigDecimal(timeoutMinutes));
        alarm.setAlarmStatus("0");
        alarm.setAlarmMessage(StringUtils.format("Agent心跳超时超过{}分钟，服务器已标记离线", timeoutMinutes));
        alarm.setAlarmTime(new Date());
        alarm.setCreateBy("system");
        monitorAlarmMapper.insertMonitorAlarm(alarm);
    }
}
