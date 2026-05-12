package com.ruoyi.web.controller.monitor;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.monitor.MonitorServer;
import com.ruoyi.system.service.IMonitorServerService;

/**
 * 监控服务器管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class MonitorServerController extends BaseController {

    @Autowired
    private IMonitorServerService monitorServerService;

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorServer monitorServer) {
        startPage();
        List<MonitorServer> list = monitorServerService.selectMonitorServerList(monitorServer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:export')")
    @Log(title = "服务器监控", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MonitorServer monitorServer) {
        List<MonitorServer> list = monitorServerService.selectMonitorServerList(monitorServer);
        ExcelUtil<MonitorServer> util = new ExcelUtil<MonitorServer>(MonitorServer.class);
        util.exportExcel(response, list, "服务器数据");
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(monitorServerService.selectMonitorServerById(id));
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:add')")
    @Log(title = "服务器监控", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorServer monitorServer) {
        return toAjax(monitorServerService.insertMonitorServer(monitorServer));
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:edit')")
    @Log(title = "服务器监控", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorServer monitorServer) {
        return toAjax(monitorServerService.updateMonitorServer(monitorServer));
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:remove')")
    @Log(title = "服务器监控", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(monitorServerService.deleteMonitorServerByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping("/online")
    public AjaxResult getOnlineServers() {
        return success(monitorServerService.selectMonitorServerOnlineList());
    }
}
