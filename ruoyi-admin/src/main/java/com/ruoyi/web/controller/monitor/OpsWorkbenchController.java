package com.ruoyi.web.controller.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IOpsWorkbenchService;

@RestController
@RequestMapping("/monitor/workbench")
public class OpsWorkbenchController extends BaseController {

    @Autowired
    private IOpsWorkbenchService opsWorkbenchService;

    @PreAuthorize("@ss.hasPermi('monitor:server:list') or @ss.hasPermi('asset:info:list')")
    @GetMapping("/summary")
    public AjaxResult summary() {
        return success(opsWorkbenchService.selectWorkbenchSummary());
    }
}
