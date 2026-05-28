package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.monitor.MonitorAlarm;
import com.ruoyi.system.domain.monitor.MonitorData;
import com.ruoyi.system.domain.monitor.MonitorServer;
import com.ruoyi.system.mapper.MonitorAlarmMapper;
import com.ruoyi.system.mapper.MonitorServerMapper;
import com.ruoyi.system.service.IMonitorDataService;
import com.ruoyi.system.service.IMonitorServerService;
import com.ruoyi.system.service.monitor.SshCredentialCipher;
import com.ruoyi.system.service.monitor.SshMonitorCollector;

/**
 * 服务器监控Service实现
 */
@Service
public class MonitorServerServiceImpl implements IMonitorServerService {

    @Autowired
    private MonitorServerMapper monitorServerMapper;

    @Autowired
    private MonitorAlarmMapper monitorAlarmMapper;

    @Autowired
    private IMonitorDataService monitorDataService;

    @Autowired
    private SshCredentialCipher credentialCipher;

    @Autowired
    private SshMonitorCollector sshMonitorCollector;

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
        prepareForSave(monitorServer, true);
        monitorServer.setCreateBy(SecurityUtils.getUsername());
        return monitorServerMapper.insertMonitorServer(monitorServer);
    }

    @Override
    @Transactional
    public int updateMonitorServer(MonitorServer monitorServer) {
        prepareForSave(monitorServer, false);
        monitorServer.setUpdateBy(SecurityUtils.getUsername());
        return monitorServerMapper.updateMonitorServer(monitorServer);
    }

    @Override
    public int updateCollectTime(Long serverId) {
        return monitorServerMapper.updateCollectTime(serverId);
    }

    @Override
    public void testSshConnection(Long id) {
        MonitorServer server = authServer(id);
        sshMonitorCollector.testConnection(server);
        monitorServerMapper.updateCollectStatus(server.getId(), "2", "0", server.getLastCollectTime());
    }

    @Override
    @Transactional
    public MonitorData collectSshMonitorData(Long id) {
        MonitorServer server = authServer(id);
        try {
            MonitorData data = sshMonitorCollector.collect(server);
            return monitorDataService.reportMonitorData(data);
        } catch (ServiceException e) {
            markConnectionFailure(server, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public int collectAllSshMonitorData() {
        List<MonitorServer> servers = monitorServerMapper.selectMonitorServerList(new MonitorServer());
        int success = 0;
        for (MonitorServer server : servers) {
            try {
                collectSshMonitorData(server.getId());
                success++;
            } catch (ServiceException e) {
                // Scheduled collection keeps moving even if a single host is unreachable.
            }
        }
        return success;
    }

    @Override
    public Map<String, Object> executePresetCommand(Long id, String commandKey) {
        MonitorServer server = authServer(id);
        String output = sshMonitorCollector.executePresetCommand(server, commandKey);
        Map<String, Object> result = new HashMap<>();
        result.put("serverId", server.getId());
        result.put("serverIp", server.getServerIp());
        result.put("commandKey", commandKey);
        result.put("output", output);
        result.put("executeTime", new Date());
        return result;
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
        return StringUtils.isNull(info) || info.getId().longValue() == id.longValue();
    }

    private void prepareForSave(MonitorServer monitorServer, boolean insert) {
        if (StringUtils.isEmpty(monitorServer.getServerIp())) {
            throw new ServiceException("服务器IP不能为空");
        }
        if (!checkServerIpUnique(monitorServer)) {
            throw new ServiceException("服务器IP已存在");
        }
        if (monitorServer.getSshPort() == null) {
            monitorServer.setSshPort(22);
        }
        if (StringUtils.isEmpty(monitorServer.getMonitorStatus())) {
            monitorServer.setMonitorStatus("2");
        }
        if (StringUtils.isEmpty(monitorServer.getConnectionStatus())) {
            monitorServer.setConnectionStatus("1");
        }
        if (StringUtils.isNotEmpty(monitorServer.getSshPassword())) {
            monitorServer.setSshPassword(credentialCipher.encrypt(monitorServer.getSshPassword()));
        } else {
            monitorServer.setSshPassword(null);
        }
        if (insert && StringUtils.isEmpty(monitorServer.getHostname())) {
            monitorServer.setHostname(monitorServer.getServerIp());
        }
        monitorServer.setAgentPath(null);
        monitorServer.setAgentToken(null);
        monitorServer.setAgentEnabled("1");
    }

    private MonitorServer authServer(Long id) {
        MonitorServer server = monitorServerMapper.selectMonitorServerAuthById(id);
        if (server == null) {
            throw new ServiceException("服务器不存在");
        }
        return server;
    }

    private void markConnectionFailure(MonitorServer server, String message) {
        monitorServerMapper.updateConnectionFailure(server.getId());
        if (monitorAlarmMapper.checkAlarmExists(server.getId(), "5") > 0) {
            return;
        }
        MonitorAlarm alarm = new MonitorAlarm();
        alarm.setServerId(server.getId());
        alarm.setServerIp(server.getServerIp());
        alarm.setHostname(server.getHostname());
        alarm.setAlarmType("5");
        alarm.setAlarmLevel("3");
        alarm.setThresholdValue(BigDecimal.ONE);
        alarm.setAlarmValue(BigDecimal.ONE);
        alarm.setAlarmStatus("0");
        alarm.setAlarmMessage("SSH连接或采集失败：" + message);
        alarm.setAlarmTime(new Date());
        alarm.setCreateBy("system");
        monitorAlarmMapper.insertMonitorAlarm(alarm);
    }
}
