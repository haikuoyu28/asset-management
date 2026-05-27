package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.OpsWorkbenchMapper;
import com.ruoyi.system.service.IOpsWorkbenchService;

@Service
public class OpsWorkbenchServiceImpl implements IOpsWorkbenchService {

    @Autowired
    private OpsWorkbenchMapper opsWorkbenchMapper;

    @Override
    public Map<String, Object> selectWorkbenchSummary() {
        int assetTotal = opsWorkbenchMapper.selectAssetCount();
        int serverTotal = opsWorkbenchMapper.selectServerCount();
        int onlineServers = opsWorkbenchMapper.selectOnlineServerCount();
        int abnormalServers = opsWorkbenchMapper.selectAbnormalServerCount();
        int alarmTotal = opsWorkbenchMapper.selectUnhandledAlarmCount();
        int flowTotal = opsWorkbenchMapper.selectAssetFlowCount();

        Map<String, Object> summary = new HashMap<>();
        summary.put("metrics", buildMetrics(assetTotal, serverTotal, onlineServers, alarmTotal, flowTotal));
        summary.put("healthItems", buildHealthItems(onlineServers, Math.max(serverTotal - onlineServers, 0), abnormalServers));
        summary.put("resourceSeries", buildResourceSeries(opsWorkbenchMapper.selectRecentMonitorMetrics()));
        summary.put("alarmEvents", opsWorkbenchMapper.selectRecentAlarms());
        summary.put("assetFlows", opsWorkbenchMapper.selectRecentAssetFlows());
        return summary;
    }

    private Map<String, Object> buildMetrics(int assetTotal, int serverTotal, int onlineServers, int alarmTotal, int flowTotal) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("assetTotal", assetTotal);
        metrics.put("serverTotal", serverTotal);
        metrics.put("onlineServers", onlineServers);
        metrics.put("onlineRate", serverTotal == 0 ? 0 : Math.round((onlineServers * 100.0F) / serverTotal));
        metrics.put("alarmTotal", alarmTotal);
        metrics.put("flowTotal", flowTotal);
        return metrics;
    }

    private List<Map<String, Object>> buildHealthItems(int online, int offline, int abnormal) {
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(healthItem("在线", online, "good"));
        items.add(healthItem("离线", offline, "warn"));
        items.add(healthItem("异常", abnormal, "bad"));
        return items;
    }

    private Map<String, Object> healthItem(String label, int value, String type) {
        Map<String, Object> item = new HashMap<>();
        item.put("label", label);
        item.put("value", value);
        item.put("type", type);
        return item;
    }

    private Map<String, Object> buildResourceSeries(List<Map<String, Object>> rows) {
        List<Map<String, Object>> orderedRows = new ArrayList<>(rows);
        Collections.reverse(orderedRows);

        List<Object> time = new ArrayList<>();
        List<Object> cpu = new ArrayList<>();
        List<Object> memory = new ArrayList<>();
        List<Object> disk = new ArrayList<>();

        for (Map<String, Object> row : orderedRows) {
            time.add(row.get("timeLabel"));
            cpu.add(row.get("cpu"));
            memory.add(row.get("memory"));
            disk.add(row.get("disk"));
        }

        Map<String, Object> series = new HashMap<>();
        series.put("time", time);
        series.put("cpu", cpu);
        series.put("memory", memory);
        series.put("disk", disk);
        return series;
    }
}
