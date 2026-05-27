# Monitor Agent

This is the minimal host agent for the asset management platform.

It collects CPU, memory, disk, load, process count, and basic network throughput, then reports to:

```text
POST /monitor/data/agent/report
Header: X-Agent-Token: <server agent token>
```

## Quick Start

1. In the web UI, open **监控中心 -> 服务器管理 -> Agent**.
2. Click **重置 Token 并生成配置**.
3. Save the generated JSON as `config.json` on the target server.
4. Run:

```bash
python3 monitor_agent.py --config config.json
```

The agent has no required third-party dependency. If `psutil` is installed, it will use it for more accurate metrics; otherwise it falls back to Linux `/proc` readers.

Keep `config.json` out of Git because it contains the server Agent Token.
