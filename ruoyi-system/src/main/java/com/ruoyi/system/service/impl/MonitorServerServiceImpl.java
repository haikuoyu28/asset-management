package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
