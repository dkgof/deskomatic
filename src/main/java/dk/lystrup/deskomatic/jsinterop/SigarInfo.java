/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 *
 * @author Gof
 */
public class SigarInfo {

    private static SigarInfo instance;

    public static SigarInfo instance() {
        if (instance == null) {
            instance = new SigarInfo();
        }

        return instance;
    }

    private Sigar sigar;

    private SigarInfo() {
        final File location = new File("./sigar-native");
        try {
            SigarProvisioner.provision(location);
        } catch (Exception ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        sigar = new Sigar();
    }

    public String getCpuInfo() {
        JsonObject json = new JsonObject();

        try {
            CpuPerc cpu = sigar.getCpuPerc();

            json.add("cpuIdle", JSBridge.getJsonWithUnits(cpu.getIdle(), null, "Cpu idle", 2));
            json.add("cpuUser", JSBridge.getJsonWithUnits(cpu.getUser(), null, "Cpu user", 2));
            json.add("cpuSystem", JSBridge.getJsonWithUnits(cpu.getSys(), null, "Cpu system", 2));
            json.add("cpuWait", JSBridge.getJsonWithUnits(cpu.getWait(), null, "Cpu wait", 2));
            json.add("cpuIrq", JSBridge.getJsonWithUnits(cpu.getIrq(), null, "Cpu irq", 2));
            json.add("cpuNice", JSBridge.getJsonWithUnits(cpu.getNice(), null, "Cpu nice", 2));
            json.add("cpuCombined", JSBridge.getJsonWithUnits(cpu.getCombined(), null, "Cpu combined", 2));

        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return json.toString();
    }

    public String getMemoryInfo() {
        JsonObject json = new JsonObject();

        try {
            Mem mem = sigar.getMem();

            json.add("memTotal", JSBridge.getJsonWithUnits(mem.getTotal() / 1024.0 / 1024.0 / 1024.0, "GiB", "Total", 3));
            json.add("memFree", JSBridge.getJsonWithUnits(mem.getActualFree() / 1024.0 / 1024.0 / 1024.0, "GiB", "Free", 3));
            json.add("memUsed", JSBridge.getJsonWithUnits(mem.getActualUsed() / 1024.0 / 1024.0 / 1024.0, "GiB", "Used", 3));

        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return json.toString();
    }

    private long lastDiskTimestamp = -1;
    private Map<String, Long> lastReadBytesMap = new HashMap<>();
    private Map<String, Long> lastWriteBytesMap = new HashMap<>();

    public String getDiskInfo() {
        JsonObject json = new JsonObject();

        try {
            FileSystem[] fsList = sigar.getFileSystemList();

            for (FileSystem fs : fsList) {
                JsonObject fsJson = new JsonObject();

                try {
                
                    FileSystemUsage fsInfo = sigar.getFileSystemUsage(fs.getDirName());

                    fsJson.add("usePercent", JSBridge.getJsonWithUnits(fsInfo.getUsePercent() * 100.0, "%", "Percent used", 1));
                    fsJson.add("free", JSBridge.getJsonWithUnits(fsInfo.getFree() / 1024.0 / 1024, "GiB", "Free space", 1));
                    fsJson.add("used", JSBridge.getJsonWithUnits(fsInfo.getUsed() / 1024.0 / 1024, "GiB", "Used space", 1));
                    fsJson.add("total", JSBridge.getJsonWithUnits(fsInfo.getTotal() / 1024.0 / 1024, "GiB", "Total space", 1));

                    long writeBytes = fsInfo.getDiskWriteBytes();
                    long readBytes = fsInfo.getDiskReadBytes();

                    if (lastDiskTimestamp != -1) {

                        long lastReadBytes = lastReadBytesMap.get(fs.getDirName());
                        long lastWriteBytes = lastWriteBytesMap.get(fs.getDirName());

                        double time = (System.currentTimeMillis() - lastDiskTimestamp) / 1000.0;
                        double writeSpeed = (writeBytes - lastWriteBytes) / time;
                        double readSpeed = (readBytes - lastReadBytes) / time;

                        fsJson.add("writeSpeed", JSBridge.getJsonWithUnits(writeSpeed / 1024, "KiB/s", "Write speed", 1));
                        fsJson.add("readSpeed", JSBridge.getJsonWithUnits(readSpeed / 1024, "KiB/s", "Read speed", 1));
                    }

                    lastReadBytesMap.put(fs.getDirName(), readBytes);
                    lastWriteBytesMap.put(fs.getDirName(), writeBytes);

                    json.add(fs.getDevName(), fsJson);
                } catch(Exception e) {
                    System.out.println("Unable to read disk: "+fs.getDirName());
                }
            }
        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        lastDiskTimestamp = System.currentTimeMillis();

        return json.toString();
    }

    private long lastNetTimestamp = -1;
    private long lastRx = -1;
    private long lastTx = -1;

    public String getNetworkInfo() {
        JsonObject json = new JsonObject();

        try {
            NetInterfaceConfig netIface = sigar.getNetInterfaceConfig(null);
            
            String desc = netIface.getDescription();
            
            json.add("description", JSBridge.getJsonWithUnits("", null, desc, -1));
            json.add("hwaddr", JSBridge.getJsonWithUnits(netIface.getHwaddr(), null, "Mac", -1));
            json.add("ip", JSBridge.getJsonWithUnits(netIface.getAddress(), null, "Ip", -1));
            json.add("netmask", JSBridge.getJsonWithUnits(netIface.getNetmask(), null, "Netmask", -1));

            NetInterfaceStat stats = sigar.getNetInterfaceStat(netIface.getName());

            long rx = stats.getRxBytes();
            long tx = stats.getTxBytes();

            if (lastNetTimestamp != -1) {
                double time = (System.currentTimeMillis() - lastNetTimestamp) / 1000.0;

                double rxSpeed = (rx - lastRx) / time / 1024.0 / 1024.0;
                double txSpeed = (tx - lastTx) / time / 1024.0 / 1024.0;

                json.add("rxSpeed", JSBridge.getJsonWithUnits(rxSpeed, "MiB/s", "Incomming", 2));
                json.add("txSpeed", JSBridge.getJsonWithUnits(txSpeed, "MiB/s", "Outgoing", 2));
            }

            lastRx = rx;
            lastTx = tx;

        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        lastNetTimestamp = System.currentTimeMillis();

        return json.toString();
    }
}
