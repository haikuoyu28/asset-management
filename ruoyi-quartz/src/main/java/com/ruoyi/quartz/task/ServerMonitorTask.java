package com.ruoyi.quartz.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.IMonitorDataService;
import com.ruoyi.system.service.IMonitorServerService;

/**
 * Server monitor scheduled tasks.
 */
@Component("serverMonitorTask")
public class ServerMonitorTask {

    private static final int DEFAULT_AGENT_TIMEOUT_MINUTES = 3;

    @Autowired
    private IMonitorServerService monitorServerService;

    @Autowired
    private IMonitorDataService monitorDataService;

    public void checkAgentHeartbeat() {
        monitorServerService.checkAgentHeartbeatTimeout(DEFAULT_AGENT_TIMEOUT_MINUTES);
    }

    public void collectMonitorData() {
        checkAgentHeartbeat();
    }

    public void cleanExpiredData() {
        monitorDataService.deleteExpiredData();
    }
}
