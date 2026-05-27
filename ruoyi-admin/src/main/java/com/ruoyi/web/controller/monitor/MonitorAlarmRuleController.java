package com.ruoyi.web.controller.monitor;

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
import com.ruoyi.system.domain.monitor.MonitorAlarmRule;
import com.ruoyi.system.service.IMonitorAlarmRuleService;

/**
 * Monitor alarm rule management.
 */
@RestController
@RequestMapping("/monitor/rule")
public class MonitorAlarmRuleController extends BaseController {

    @Autowired
    private IMonitorAlarmRuleService monitorAlarmRuleService;

    @PreAuthorize("@ss.hasPermi('monitor:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorAlarmRule monitorAlarmRule) {
        startPage();
        List<MonitorAlarmRule> list = monitorAlarmRuleService.selectMonitorAlarmRuleList(monitorAlarmRule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:rule:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(monitorAlarmRuleService.selectMonitorAlarmRuleById(id));
    }

    @PreAuthorize("@ss.hasPermi('monitor:rule:add')")
    @Log(title = "告警规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorAlarmRule monitorAlarmRule) {
        return toAjax(monitorAlarmRuleService.insertMonitorAlarmRule(monitorAlarmRule));
    }

    @PreAuthorize("@ss.hasPermi('monitor:rule:edit')")
    @Log(title = "告警规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorAlarmRule monitorAlarmRule) {
        return toAjax(monitorAlarmRuleService.updateMonitorAlarmRule(monitorAlarmRule));
    }

    @PreAuthorize("@ss.hasPermi('monitor:rule:remove')")
    @Log(title = "告警规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(monitorAlarmRuleService.deleteMonitorAlarmRuleByIds(ids));
    }
}
