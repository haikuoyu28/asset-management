package com.ruoyi.system.domain.asset;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资产流程对象 asset_flow
 *
 * @author ruoyi
 */
public class AssetFlow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private String flowType;
    private String operator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
    private String beforePerson;
    private String afterPerson;
    private String beforeStatus;
    private String afterStatus;
    private String description;
    private String attachment;
    private String delFlag;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getBeforePerson() {
        return beforePerson;
    }

    public void setBeforePerson(String beforePerson) {
        this.beforePerson = beforePerson;
    }

    public String getAfterPerson() {
        return afterPerson;
    }

    public void setAfterPerson(String afterPerson) {
        this.afterPerson = afterPerson;
    }

    public String getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}