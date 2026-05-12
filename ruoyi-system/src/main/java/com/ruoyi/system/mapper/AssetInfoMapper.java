package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.asset.AssetInfo;

/**
 * 资产管理Mapper接口
 *
 * @author ruoyi
 */
public interface AssetInfoMapper {

    public AssetInfo selectAssetInfoById(Long id);

    public List<AssetInfo> selectAssetInfoList(AssetInfo assetInfo);

    public int insertAssetInfo(AssetInfo assetInfo);

    public int updateAssetInfo(AssetInfo assetInfo);

    public int deleteAssetInfoById(Long id);

    public int deleteAssetInfoByIds(Long[] ids);

    public AssetInfo selectAssetInfoByCode(@Param("assetCode") String assetCode);

    public int checkAssetCodeUnique(@Param("assetCode") String assetCode, @Param("id") Long id);
}
