package com.ruoyi.web.controller.asset;

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
import com.ruoyi.system.domain.asset.AssetInfo;
import com.ruoyi.system.service.IAssetInfoService;

/**
 * 资产管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/asset/info")
public class AssetInfoController extends BaseController {

    @Autowired
    private IAssetInfoService assetInfoService;

    @PreAuthorize("@ss.hasPermi('asset:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(AssetInfo assetInfo) {
        startPage();
        List<AssetInfo> list = assetInfoService.selectAssetInfoList(assetInfo);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('asset:info:export')")
    @Log(title = "资产管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AssetInfo assetInfo) {
        List<AssetInfo> list = assetInfoService.selectAssetInfoListForExport(assetInfo);
        ExcelUtil<AssetInfo> util = new ExcelUtil<AssetInfo>(AssetInfo.class);
        util.exportExcel(response, list, "资产数据");
    }

    @PreAuthorize("@ss.hasPermi('asset:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(assetInfoService.selectAssetInfoById(id));
    }

    @PreAuthorize("@ss.hasPermi('asset:info:add')")
    @Log(title = "资产管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AssetInfo assetInfo) {
        return toAjax(assetInfoService.insertAssetInfo(assetInfo));
    }

    @PreAuthorize("@ss.hasPermi('asset:info:edit')")
    @Log(title = "资产管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AssetInfo assetInfo) {
        return toAjax(assetInfoService.updateAssetInfo(assetInfo));
    }

    @PreAuthorize("@ss.hasPermi('asset:info:remove')")
    @Log(title = "资产管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(assetInfoService.deleteAssetInfoByIds(ids));
    }
}
