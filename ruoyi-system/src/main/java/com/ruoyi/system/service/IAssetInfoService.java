package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.asset.AssetInfo;

/**
 * 资产管理Service接口
 *
 * @author ruoyi
 */
public interface IAssetInfoService {

    public AssetInfo selectAssetInfoById(Long id);

    public List<AssetInfo> selectAssetInfoList(AssetInfo assetInfo);

    public int insertAssetInfo(AssetInfo assetInfo);

    public int updateAssetInfo(AssetInfo assetInfo);

    public int deleteAssetInfoById(Long id);

    public int deleteAssetInfoByIds(Long[] ids);

    public boolean checkAssetCodeUnique(AssetInfo assetInfo);

    public List<AssetInfo> selectAssetInfoListForExport(AssetInfo assetInfo);
}
