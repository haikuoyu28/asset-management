package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

public interface OpsWorkbenchMapper {

    int selectAssetCount();

    int selectServerCount();

    int selectOnlineServerCount();

    int selectAbnormalServerCount();

    int selectUnhandledAlarmCount();

    int selectAssetFlowCount();

    List<Map<String, Object>> selectRecentAlarms();

    List<Map<String, Object>> selectRecentAssetFlows();

    List<Map<String, Object>> selectRecentMonitorMetrics();
}
