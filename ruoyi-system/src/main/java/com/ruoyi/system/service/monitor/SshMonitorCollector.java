package com.ruoyi.system.service.monitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.monitor.MonitorData;
import com.ruoyi.system.domain.monitor.MonitorServer;

@Component
public class SshMonitorCollector {

    private static final int CONNECT_TIMEOUT_MS = 8000;
    private static final int COMMAND_TIMEOUT_SECONDS = 15;

    @Autowired
    private SshCredentialCipher credentialCipher;

    public void testConnection(MonitorServer server) {
        try (SSHClient ssh = openClient(server)) {
            String output = execute(ssh, "echo ssh-ok", 5);
            if (!output.trim().contains("ssh-ok")) {
                throw new ServiceException("SSH连接测试未返回预期结果");
            }
        } catch (IOException e) {
            throw new ServiceException("SSH连接失败：" + e.getMessage());
        }
    }

    public MonitorData collect(MonitorServer server) {
        try (SSHClient ssh = openClient(server)) {
            MonitorData data = new MonitorData();
            data.setServerId(server.getId());
            data.setServerIp(server.getServerIp());

            long[] firstCpu = readCpuSample(ssh);
            sleepQuietly(1000);
            long[] secondCpu = readCpuSample(ssh);
            data.setCpuUsage(calculateCpuUsage(firstCpu, secondCpu));

            applyMemory(data, execute(ssh,
                    "awk '/MemTotal/{t=$2}/MemAvailable/{a=$2}END{printf \"%.2f %.2f %.2f\", (t-a)/t*100, (t-a)/1048576, t/1048576}' /proc/meminfo",
                    COMMAND_TIMEOUT_SECONDS));
            applyDisk(data, execute(ssh,
                    "df -Pk / | awk 'NR==2{printf \"%.2f %.2f %.2f\", $5+0, $3/1048576, $2/1048576}'",
                    COMMAND_TIMEOUT_SECONDS));
            data.setLoadAverage(execute(ssh,
                    "awk '{print $1\", \"$2\", \"$3}' /proc/loadavg",
                    COMMAND_TIMEOUT_SECONDS).trim());
            data.setRunningProcesses(parseInteger(execute(ssh,
                    "find /proc -maxdepth 1 -regex '/proc/[0-9]+' 2>/dev/null | wc -l",
                    COMMAND_TIMEOUT_SECONDS)));
            return data;
        } catch (IOException e) {
            throw new ServiceException("SSH采集失败：" + e.getMessage());
        }
    }

    public String executePresetCommand(MonitorServer server, String commandKey) {
        String command = presetCommand(commandKey);
        try (SSHClient ssh = openClient(server)) {
            return execute(ssh, command, 20);
        } catch (IOException e) {
            throw new ServiceException("远程命令执行失败：" + e.getMessage());
        }
    }

    private SSHClient openClient(MonitorServer server) throws IOException {
        validateServer(server);
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.setConnectTimeout(CONNECT_TIMEOUT_MS);
        ssh.setTimeout(CONNECT_TIMEOUT_MS);
        ssh.connect(server.getServerIp(), server.getSshPort() == null ? 22 : server.getSshPort());
        ssh.authPassword(server.getSshUsername(), credentialCipher.decrypt(server.getSshPassword()));
        return ssh;
    }

    private void validateServer(MonitorServer server) {
        if (server == null) {
            throw new ServiceException("服务器不存在");
        }
        if (StringUtils.isEmpty(server.getServerIp())) {
            throw new ServiceException("服务器IP不能为空");
        }
        if (StringUtils.isEmpty(server.getSshUsername())) {
            throw new ServiceException("SSH用户名不能为空");
        }
        if (StringUtils.isEmpty(server.getSshPassword())) {
            throw new ServiceException("SSH密码不能为空");
        }
    }

    private String execute(SSHClient ssh, String command, int timeoutSeconds) throws IOException {
        try (Session session = ssh.startSession()) {
            Session.Command cmd = session.exec("sh -lc '" + escapeSingleQuotes(command) + "'");
            String output = readFully(cmd.getInputStream());
            String error = readFully(cmd.getErrorStream());
            cmd.join(timeoutSeconds, TimeUnit.SECONDS);
            Integer exitStatus = cmd.getExitStatus();
            if (exitStatus != null && exitStatus != 0) {
                throw new ServiceException(StringUtils.isNotEmpty(error) ? error.trim() : "命令退出码：" + exitStatus);
            }
            return output;
        }
    }

    private String readFully(java.io.InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private String escapeSingleQuotes(String command) {
        return command.replace("'", "'\"'\"'");
    }

    private long[] readCpuSample(SSHClient ssh) throws IOException {
        String[] parts = execute(ssh,
                "awk '/^cpu /{print $2+$3+$4+$5+$6+$7+$8, $5+$6; exit}' /proc/stat",
                COMMAND_TIMEOUT_SECONDS).trim().split("\\s+");
        if (parts.length < 2) {
            throw new ServiceException("无法读取CPU采样数据");
        }
        return new long[] { Long.parseLong(parts[0]), Long.parseLong(parts[1]) };
    }

    private BigDecimal calculateCpuUsage(long[] first, long[] second) {
        long totalDelta = second[0] - first[0];
        long idleDelta = second[1] - first[1];
        if (totalDelta <= 0) {
            return BigDecimal.ZERO;
        }
        return scale(BigDecimal.valueOf((1D - idleDelta * 1D / totalDelta) * 100D));
    }

    private void applyMemory(MonitorData data, String output) {
        String[] parts = output.trim().split("\\s+");
        if (parts.length >= 3) {
            data.setMemoryUsage(decimal(parts[0]));
            data.setMemoryUsedGb(decimal(parts[1]));
            data.setMemoryTotalGb(decimal(parts[2]));
        }
    }

    private void applyDisk(MonitorData data, String output) {
        String[] parts = output.trim().split("\\s+");
        if (parts.length >= 3) {
            data.setDiskUsage(decimal(parts[0]));
            data.setDiskUsedGb(decimal(parts[1]));
            data.setDiskTotalGb(decimal(parts[2]));
        }
    }

    private BigDecimal decimal(String value) {
        return scale(new BigDecimal(value));
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String presetCommand(String commandKey) {
        if ("disk".equals(commandKey)) {
            return "df -hP";
        }
        if ("memory".equals(commandKey)) {
            return "free -h";
        }
        if ("load".equals(commandKey)) {
            return "uptime && cat /proc/loadavg";
        }
        if ("process".equals(commandKey)) {
            return "ps -eo pid,ppid,cmd,%mem,%cpu --sort=-%cpu | head -n 20";
        }
        if ("port".equals(commandKey)) {
            return "ss -tunlp 2>/dev/null | head -n 40 || netstat -tunlp 2>/dev/null | head -n 40";
        }
        throw new ServiceException("不支持的预设命令");
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
