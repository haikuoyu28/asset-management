package com.ruoyi.web.controller.asset;

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
import com.ruoyi.system.domain.asset.AssetFlow;
import com.ruoyi.system.domain.asset.AssetInfo;
import com.ruoyi.system.service.IAssetFlowService;
import com.ruoyi.system.service.IAssetInfoService;

/**
 * 资产流程管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/asset/flow")
public class AssetFlowController extends BaseController {

    @Autowired
    private IAssetFlowService assetFlowService;

    @Autowired
    private IAssetInfoService assetInfoService;

    @PreAuthorize("@ss.hasPermi('asset:flow:list')")
    @GetMapping("/list")
    public TableDataInfo list(AssetFlow assetFlow) {
        startPage();
        List<AssetFlow> list = assetFlowService.selectAssetFlowList(assetFlow);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('asset:flow:list')")
    @GetMapping("/list/{assetId}")
    public AjaxResult listByAssetId(@PathVariable Long assetId) {
        return success(assetFlowService.selectAssetFlowByAssetId(assetId));
    }

    @PreAuthorize("@ss.hasPermi('asset:flow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(assetFlowService.selectAssetFlowById(id));
    }

    @PreAuthorize("@ss.hasPermi('asset:flow:add')")
    @Log(title = "资产流程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AssetFlow assetFlow) {
        AssetInfo assetInfo = assetInfoService.selectAssetInfoById(assetFlow.getAssetId());
        if (assetInfo == null) {
            return error("资产不存在");
        }
        assetFlow.setAssetName(assetInfo.getAssetName());
        assetFlow.setAssetCode(assetInfo.getAssetCode());
        assetFlow.setBeforeStatus(assetInfo.getStatus());
        assetFlow.setAfterStatus(assetInfo.getStatus());
        int result = assetFlowService.insertAssetFlow(assetFlow);

        if (result > 0 && assetFlow.getAfterStatus() != null && !assetFlow.getAfterStatus().equals(assetFlow.getBeforeStatus())) {
            assetInfo.setStatus(assetFlow.getAfterStatus());
            assetInfoService.updateAssetInfo(assetInfo);
        }
        return toAjax(result);
    }

    @PreAuthorize("@ss.hasPermi('asset:flow:edit')")
    @Log(title = "资产流程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AssetFlow assetFlow) {
        return toAjax(assetFlowService.updateAssetFlow(assetFlow));
    }

    @PreAuthorize("@ss.hasPermi('asset:flow:remove')")
    @Log(title = "资产流程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(assetFlowService.deleteAssetFlowByIds(ids));
    }
}
