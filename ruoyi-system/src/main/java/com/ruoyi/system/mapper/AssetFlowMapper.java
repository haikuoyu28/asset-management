package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.asset.AssetFlow;

/**
 * 资产流程Mapper接口
 *
 * @author ruoyi
 */
public interface AssetFlowMapper {

    public AssetFlow selectAssetFlowById(Long id);

    public List<AssetFlow> selectAssetFlowList(AssetFlow assetFlow);

    public List<AssetFlow> selectAssetFlowByAssetId(Long assetId);

    public int insertAssetFlow(AssetFlow assetFlow);

    public int updateAssetFlow(AssetFlow assetFlow);

    public int deleteAssetFlowById(Long id);

    public int deleteAssetFlowByIds(Long[] ids);

    public int deleteAssetFlowByAssetId(Long assetId);
}
