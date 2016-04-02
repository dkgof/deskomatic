/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
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
        if(instance == null) {
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
            
            json.addProperty("cpuIdle", cpu.getIdle());
            json.addProperty("cpuCombined", cpu.getCombined());
            json.addProperty("cpuWait", cpu.getWait());
            json.addProperty("cpuSystem", cpu.getSys());
            json.addProperty("cpuUser", cpu.getUser());
            json.addProperty("cpuIrq", cpu.getIrq());
            json.addProperty("cpuNice", cpu.getNice());
            
        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return json.toString();
    }
    
    public String getDiskInfo() {
        JsonObject json = new JsonObject();
        
        try {
            FileSystem[] fsList = sigar.getFileSystemList();
            
            for(FileSystem fs : fsList) {
                JsonObject fsJson = new JsonObject();
                
                FileSystemUsage fsInfo = sigar.getFileSystemUsage(fs.getDirName());
                
                fsJson.addProperty("usePercent", fsInfo.getUsePercent());
                fsJson.addProperty("free", fsInfo.getFree());
                fsJson.addProperty("used", fsInfo.getUsed());
                fsJson.addProperty("total", fsInfo.getTotal());
                
                json.add(fs.getDevName(), fsJson);
            }
        } catch (SigarException ex) {
            Logger.getLogger(SigarInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return json.toString();
    }
}
