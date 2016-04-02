/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 *
 * @author Gof
 */
public class SigarInfo {
    private Sigar sigar;
    
    public SigarInfo() {
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
}
