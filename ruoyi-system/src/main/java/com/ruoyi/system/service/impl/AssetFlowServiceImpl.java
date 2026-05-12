package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.AssetFlowMapper;
import com.ruoyi.system.domain.asset.AssetFlow;
import com.ruoyi.system.service.IAssetFlowService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 资产流程Service实现
 *
 * @author ruoyi
 */
@Service
public class AssetFlowServiceImpl implements IAssetFlowService {

    @Autowired
    private AssetFlowMapper assetFlowMapper;

    @Override
    public AssetFlow selectAssetFlowById(Long id) {
        return assetFlowMapper.selectAssetFlowById(id);
    }

    @Override
    public List<AssetFlow> selectAssetFlowList(AssetFlow assetFlow) {
        return assetFlowMapper.selectAssetFlowList(assetFlow);
    }

    @Override
    public List<AssetFlow> selectAssetFlowByAssetId(Long assetId) {
        return assetFlowMapper.selectAssetFlowByAssetId(assetId);
    }

    @Override
    @Transactional
    public int insertAssetFlow(AssetFlow assetFlow) {
        assetFlow.setOperateTime(new Date());
        assetFlow.setOperator(SecurityUtils.getUsername());
        assetFlow.setCreateBy(SecurityUtils.getUsername());
        return assetFlowMapper.insertAssetFlow(assetFlow);
    }

    @Override
    @Transactional
    public int updateAssetFlow(AssetFlow assetFlow) {
        assetFlow.setUpdateBy(SecurityUtils.getUsername());
        return assetFlowMapper.updateAssetFlow(assetFlow);
    }

    @Override
    @Transactional
    public int deleteAssetFlowById(Long id) {
        return assetFlowMapper.deleteAssetFlowById(id);
    }

    @Override
    @Transactional
    public int deleteAssetFlowByIds(Long[] ids) {
        return assetFlowMapper.deleteAssetFlowByIds(ids);
    }
}
