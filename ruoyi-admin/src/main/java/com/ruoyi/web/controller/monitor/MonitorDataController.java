package com.ruoyi.web.controller.monitor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.monitor.MonitorData;
import com.ruoyi.system.service.IMonitorDataService;

/**
 * 监控数据管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/data")
public class MonitorDataController extends BaseController {

    @Autowired
    private IMonitorDataService monitorDataService;

    @PreAuthorize("@ss.hasPermi('monitor:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorData monitorData) {
        startPage();
        List<MonitorData> list = monitorDataService.selectMonitorDataList(monitorData);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:data:query')")
    @GetMapping("/latest/{serverId}")
    public AjaxResult getLatestData(@PathVariable Long serverId) {
        return success(monitorDataService.selectLatestMonitorData(serverId));
    }

    @PreAuthorize("@ss.hasPermi('monitor:data:query')")
    @GetMapping("/recent/{serverId}")
    public AjaxResult getRecentData(@PathVariable Long serverId, int limit) {
        if (limit <= 0 || limit > 100) {
            limit = 20;
        }
        List<MonitorData> list = monitorDataService.selectMonitorDataRecent(serverId, limit);
        return success(list);
    }
}
