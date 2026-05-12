package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.asset.AssetFlow;

/**
 * 资产流程Service接口
 *
 * @author ruoyi
 */
public interface IAssetFlowService {

    public AssetFlow selectAssetFlowById(Long id);

    public List<AssetFlow> selectAssetFlowList(AssetFlow assetFlow);

    public List<AssetFlow> selectAssetFlowByAssetId(Long assetId);

    public int insertAssetFlow(AssetFlow assetFlow);

    public int updateAssetFlow(AssetFlow assetFlow);

    public int deleteAssetFlowById(Long id);

    public int deleteAssetFlowByIds(Long[] ids);
}
