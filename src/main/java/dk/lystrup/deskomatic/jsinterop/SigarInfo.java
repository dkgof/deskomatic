/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import com.google.gson.JsonArray;
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

    private long lastTimestamp = -1;
    private Map<String, Long> lastReadBytesMap = new HashMap<>();
    private Map<String, Long> lastWriteBytesMap = new HashMap<>();

    public String getDiskInfo() {
        JsonObject json = new JsonObject();

        try {
            FileSystem[] fsList = sigar.getFileSystemList();

            for (FileSystem fs : fsList) {
                JsonObject fsJson = new JsonObject();

                FileSystemUsage fsInfo = sigar.getFileSystemUsage(fs.getDirName());

                fsJson.add("usePercent", JSBridge.getJsonWithUnits(fsInfo.getUsePercent() * 100.0, "%", "Percent used", 1));
                fsJson.add("free", JSBridge.getJsonWithUnits(fsInfo.getFree() / 1024.0 / 1024, "GB", "Free space", 1));
                fsJson.add("used", JSBridge.getJsonWithUnits(fsInfo.getUsed() / 1024.0 / 1024, "GB", "Used space", 1));
                fsJson.add("total", JSBridge.getJsonWithUnits(fsInfo.getTotal() / 1024.0 / 1024, "GB", "Total space", 1));

                long writeBytes = fsInfo.getDiskWriteBytes();
                long readBytes = fsInfo.getDiskReadBytes();

                if (lastTimestamp != -1) {

                    long lastReadBytes = lastReadBytesMap.get(fs.getDirName());
                    long lastWriteBytes = lastWriteBytesMap.get(fs.getDirName());

                    double time = (System.currentTimeMillis() - lastTimestamp) / 1000.0;
                    double writeSpeed = (writeBytes - lastWriteBytes) / time;
                    double readSpeed = (readBytes - lastReadBytes) / time;

                    fsJson.add("writeSpeed", JSBridge.getJsonWithUnits(writeSpeed / 1024, "KB/s", "Write speed", 1));
                    fsJson.add("readSpeed", JSBridge.getJsonWithUnits(readSpeed / 1024, "KB/s", "Read speed", 1));
                }

                lastReadBytesMap.put(fs.getDirName(), readBytes);
                lastWriteBytesMap.put(fs.getDirName(), writeBytes);

                json.add(fs.getDevName(), fsJson);
            }
        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        lastTimestamp = System.currentTimeMillis();

        return json.toString();
    }

    public String getNetworkInfo() {
        JsonObject json = new JsonObject();

        return json.toString();
    }
}
