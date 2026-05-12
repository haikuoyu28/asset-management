package com.ruoyi.web.controller.monitor;

import java.util.Date;
import java.util.List;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.monitor.MonitorAlarm;
import com.ruoyi.system.service.IMonitorAlarmService;

/**
 * 监控告警管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/alarm")
public class MonitorAlarmController extends BaseController {

    @Autowired
    private IMonitorAlarmService monitorAlarmService;

    @PreAuthorize("@ss.hasPermi('monitor:alarm:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorAlarm monitorAlarm) {
        startPage();
        List<MonitorAlarm> list = monitorAlarmService.selectMonitorAlarmList(monitorAlarm);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(monitorAlarmService.selectMonitorAlarmById(id));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:add')")
    @Log(title = "监控告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorAlarm monitorAlarm) {
        return toAjax(monitorAlarmService.insertMonitorAlarm(monitorAlarm));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:edit')")
    @Log(title = "监控告警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorAlarm monitorAlarm) {
        return toAjax(monitorAlarmService.updateMonitorAlarm(monitorAlarm));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:remove')")
    @Log(title = "监控告警", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(monitorAlarmService.deleteMonitorAlarmByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:query')")
    @GetMapping("/unhandle/count")
    public AjaxResult getUnhandleCount() {
        return success(monitorAlarmService.selectUnhandleAlarmCount());
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:query')")
    @GetMapping("/unhandle/list")
    public AjaxResult getUnhandleList() {
        return success(monitorAlarmService.selectUnhandleAlarmList());
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:edit')")
    @Log(title = "监控告警", businessType = BusinessType.UPDATE)
    @PutMapping("/handle/{id}")
    public AjaxResult handleAlarm(@PathVariable("id") Long id, @RequestBody MonitorAlarm monitorAlarm) {
        monitorAlarm.setId(id);
        monitorAlarm.setAlarmStatus("2");
        monitorAlarm.setHandleTime(new Date());
        monitorAlarm.setHandlePerson(SecurityUtils.getUsername());
        return toAjax(monitorAlarmService.updateMonitorAlarm(monitorAlarm));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alarm:edit')")
    @Log(title = "监控告警", businessType = BusinessType.UPDATE)
    @PutMapping("/ignore/{id}")
    public AjaxResult ignoreAlarm(@PathVariable("id") Long id) {
        MonitorAlarm monitorAlarm = new MonitorAlarm();
        monitorAlarm.setId(id);
        monitorAlarm.setAlarmStatus("3");
        monitorAlarm.setHandleTime(new Date());
        monitorAlarm.setHandlePerson(SecurityUtils.getUsername());
        return toAjax(monitorAlarmService.updateMonitorAlarm(monitorAlarm));
    }
}
