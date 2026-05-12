package com.ruoyi.system.domain.monitor;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监控数据对象 monitor_data
 *
 * @author ruoyi
 */
public class MonitorData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long serverId;
    private String serverIp;
    private BigDecimal cpuUsage;
    private BigDecimal memoryUsage;
    private BigDecimal memoryUsedGb;
    private BigDecimal memoryTotalGb;
    private BigDecimal diskUsage;
    private BigDecimal diskUsedGb;
    private BigDecimal diskTotalGb;
    private BigDecimal networkIn;
    private BigDecimal networkOut;
    private String loadAverage;
    private Integer runningProcesses;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public BigDecimal getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(BigDecimal cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public BigDecimal getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(BigDecimal memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public BigDecimal getMemoryUsedGb() {
        return memoryUsedGb;
    }

    public void setMemoryUsedGb(BigDecimal memoryUsedGb) {
        this.memoryUsedGb = memoryUsedGb;
    }

    public BigDecimal getMemoryTotalGb() {
        return memoryTotalGb;
    }

    public void setMemoryTotalGb(BigDecimal memoryTotalGb) {
        this.memoryTotalGb = memoryTotalGb;
    }

    public BigDecimal getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(BigDecimal diskUsage) {
        this.diskUsage = diskUsage;
    }

    public BigDecimal getDiskUsedGb() {
        return diskUsedGb;
    }

    public void setDiskUsedGb(BigDecimal diskUsedGb) {
        this.diskUsedGb = diskUsedGb;
    }

    public BigDecimal getDiskTotalGb() {
        return diskTotalGb;
    }

    public void setDiskTotalGb(BigDecimal diskTotalGb) {
        this.diskTotalGb = diskTotalGb;
    }

    public BigDecimal getNetworkIn() {
        return networkIn;
    }

    public void setNetworkIn(BigDecimal networkIn) {
        this.networkIn = networkIn;
    }

    public BigDecimal getNetworkOut() {
        return networkOut;
    }

    public void setNetworkOut(BigDecimal networkOut) {
        this.networkOut = networkOut;
    }

    public String getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(String loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Integer getRunningProcesses() {
        return runningProcesses;
    }

    public void setRunningProcesses(Integer runningProcesses) {
        this.runningProcesses = runningProcesses;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }
}