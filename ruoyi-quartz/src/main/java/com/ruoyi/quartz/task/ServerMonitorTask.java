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

    @Autowired
    private IMonitorServerService monitorServerService;

    @Autowired
    private IMonitorDataService monitorDataService;

    public void collectSshMonitorData() {
        monitorServerService.collectAllSshMonitorData();
    }

    public void collectMonitorData() {
        collectSshMonitorData();
    }

    public void cleanExpiredData() {
        monitorDataService.deleteExpiredData();
    }
}
