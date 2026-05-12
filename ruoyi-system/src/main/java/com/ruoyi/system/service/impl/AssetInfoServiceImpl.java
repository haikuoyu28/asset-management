package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.AssetInfoMapper;
import com.ruoyi.system.domain.asset.AssetInfo;
import com.ruoyi.system.service.IAssetInfoService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 资产管理Service实现
 *
 * @author ruoyi
 */
@Service
public class AssetInfoServiceImpl implements IAssetInfoService {

    @Autowired
    private AssetInfoMapper assetInfoMapper;

    @Override
    public AssetInfo selectAssetInfoById(Long id) {
        return assetInfoMapper.selectAssetInfoById(id);
    }

    @Override
    public List<AssetInfo> selectAssetInfoList(AssetInfo assetInfo) {
        return assetInfoMapper.selectAssetInfoList(assetInfo);
    }

    @Override
    @Transactional
    public int insertAssetInfo(AssetInfo assetInfo) {
        if (!checkAssetCodeUnique(assetInfo)) {
            throw new ServiceException("设备编号已存在");
        }
        assetInfo.setCreateBy(SecurityUtils.getUsername());
        return assetInfoMapper.insertAssetInfo(assetInfo);
    }

    @Override
    @Transactional
    public int updateAssetInfo(AssetInfo assetInfo) {
        if (!checkAssetCodeUnique(assetInfo)) {
            throw new ServiceException("设备编号已存在");
        }
        assetInfo.setUpdateBy(SecurityUtils.getUsername());
        return assetInfoMapper.updateAssetInfo(assetInfo);
    }

    @Override
    @Transactional
    public int deleteAssetInfoById(Long id) {
        return assetInfoMapper.deleteAssetInfoById(id);
    }

    @Override
    @Transactional
    public int deleteAssetInfoByIds(Long[] ids) {
        return assetInfoMapper.deleteAssetInfoByIds(ids);
    }

    @Override
    public boolean checkAssetCodeUnique(AssetInfo assetInfo) {
        Long id = StringUtils.isNull(assetInfo.getId()) ? -1L : assetInfo.getId();
        AssetInfo info = assetInfoMapper.selectAssetInfoByCode(assetInfo.getAssetCode());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public List<AssetInfo> selectAssetInfoListForExport(AssetInfo assetInfo) {
        return assetInfoMapper.selectAssetInfoList(assetInfo);
    }
}
