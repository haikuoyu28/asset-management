#!/usr/bin/env python3
import argparse
import json
import os
import shutil
import time
import urllib.error
import urllib.request

try:
    import psutil
except ImportError:
    psutil = None


def load_config(path):
    with open(path, "r", encoding="utf-8") as handle:
        return json.load(handle)


def read_proc_stat():
    with open("/proc/stat", "r", encoding="utf-8") as handle:
        parts = handle.readline().split()[1:]
    values = [int(item) for item in parts]
    idle = values[3] + (values[4] if len(values) > 4 else 0)
    total = sum(values)
    return idle, total


def cpu_usage(prev_sample):
    if psutil:
        return round(psutil.cpu_percent(interval=1), 2), prev_sample

    first = prev_sample or read_proc_stat()
    time.sleep(1)
    second = read_proc_stat()
    idle_delta = second[0] - first[0]
    total_delta = second[1] - first[1]
    usage = 0 if total_delta <= 0 else (1 - idle_delta / total_delta) * 100
    return round(usage, 2), second


def memory_metrics():
    if psutil:
        memory = psutil.virtual_memory()
        return {
            "memoryUsage": round(memory.percent, 2),
            "memoryUsedGb": round(memory.used / 1024 / 1024 / 1024, 2),
            "memoryTotalGb": round(memory.total / 1024 / 1024 / 1024, 2),
        }

    values = {}
    with open("/proc/meminfo", "r", encoding="utf-8") as handle:
        for line in handle:
            key, value = line.split(":", 1)
            values[key] = int(value.strip().split()[0])
    total = values.get("MemTotal", 0)
    available = values.get("MemAvailable", 0)
    used = max(total - available, 0)
    usage = 0 if total <= 0 else used / total * 100
    return {
        "memoryUsage": round(usage, 2),
        "memoryUsedGb": round(used / 1024 / 1024, 2),
        "memoryTotalGb": round(total / 1024 / 1024, 2),
    }


def disk_metrics(path):
    usage = shutil.disk_usage(path)
    used = usage.total - usage.free
    percent = 0 if usage.total <= 0 else used / usage.total * 100
    return {
        "diskUsage": round(percent, 2),
        "diskUsedGb": round(used / 1024 / 1024 / 1024, 2),
        "diskTotalGb": round(usage.total / 1024 / 1024 / 1024, 2),
    }


def read_network_bytes():
    if psutil:
        counters = psutil.net_io_counters()
        return counters.bytes_recv, counters.bytes_sent

    recv = 0
    sent = 0
    with open("/proc/net/dev", "r", encoding="utf-8") as handle:
        for line in handle.readlines()[2:]:
            if ":" not in line:
                continue
            _, payload = line.split(":", 1)
            parts = payload.split()
            recv += int(parts[0])
            sent += int(parts[8])
    return recv, sent


def network_metrics(prev_sample, interval_seconds):
    current = read_network_bytes()
    if not prev_sample:
        return {"networkIn": 0, "networkOut": 0}, current
    elapsed = max(interval_seconds, 1)
    inbound = max(current[0] - prev_sample[0], 0) / elapsed / 1024
    outbound = max(current[1] - prev_sample[1], 0) / elapsed / 1024
    return {"networkIn": round(inbound, 2), "networkOut": round(outbound, 2)}, current


def load_average():
    try:
        return ", ".join(str(round(item, 2)) for item in os.getloadavg())
    except (AttributeError, OSError):
        return None


def running_processes():
    if psutil:
        return len(psutil.pids())
    proc_path = "/proc"
    if not os.path.isdir(proc_path):
        return None
    return sum(1 for item in os.listdir(proc_path) if item.isdigit())


def collect(config, state):
    cpu, state["cpu"] = cpu_usage(state.get("cpu"))
    network, state["network"] = network_metrics(state.get("network"), config["intervalSeconds"])
    payload = {
        "serverId": config["serverId"],
        "cpuUsage": cpu,
        "loadAverage": load_average(),
        "runningProcesses": running_processes(),
    }
    payload.update(memory_metrics())
    payload.update(disk_metrics(config.get("diskPath", "/")))
    payload.update(network)
    return payload


def report(config, payload):
    body = json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(
        config["endpoint"],
        data=body,
        method="POST",
        headers={
            "Content-Type": "application/json",
            "X-Agent-Token": config["token"],
        },
    )
    with urllib.request.urlopen(request, timeout=10) as response:
        return response.status, response.read().decode("utf-8")


def run(config):
    state = {"network": read_network_bytes()}
    interval = max(int(config.get("intervalSeconds", 30)), 5)
    config["intervalSeconds"] = interval
    while True:
        started = time.time()
        try:
            payload = collect(config, state)
            status, _ = report(config, payload)
            print(f"{time.strftime('%Y-%m-%d %H:%M:%S')} report ok status={status} cpu={payload['cpuUsage']}%")
        except (OSError, urllib.error.URLError, ValueError, KeyError) as exc:
            print(f"{time.strftime('%Y-%m-%d %H:%M:%S')} report failed: {exc}")
        elapsed = time.time() - started
        time.sleep(max(interval - elapsed, 1))


def main():
    parser = argparse.ArgumentParser(description="Asset management monitor agent")
    parser.add_argument("--config", default="config.json", help="Path to agent config json")
    args = parser.parse_args()
    run(load_config(args.config))


if __name__ == "__main__":
    main()
