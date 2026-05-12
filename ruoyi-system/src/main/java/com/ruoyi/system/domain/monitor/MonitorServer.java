package com.ruoyi.system.domain.monitor;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监控服务器对象 monitor_server
 *
 * @author ruoyi
 */
public class MonitorServer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String serverIp;
    private String hostname;
    private String osName;
    private Integer cpuCores;
    private Integer memoryGb;
    private Integer diskGb;
    private String monitorStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastCollectTime;
    private String connectionStatus;
    private Integer sshPort;
    private String sshUsername;
    private String sshPassword;
    private String agentPath;
    private String delFlag;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Integer getMemoryGb() {
        return memoryGb;
    }

    public void setMemoryGb(Integer memoryGb) {
        this.memoryGb = memoryGb;
    }

    public Integer getDiskGb() {
        return diskGb;
    }

    public void setDiskGb(Integer diskGb) {
        this.diskGb = diskGb;
    }

    public String getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(String monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    public Date getLastCollectTime() {
        return lastCollectTime;
    }

    public void setLastCollectTime(Date lastCollectTime) {
        this.lastCollectTime = lastCollectTime;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshUsername() {
        return sshUsername;
    }

    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getAgentPath() {
        return agentPath;
    }

    public void setAgentPath(String agentPath) {
        this.agentPath = agentPath;
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