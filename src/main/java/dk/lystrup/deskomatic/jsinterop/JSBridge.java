/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import com.google.gson.JsonObject;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessControlException;
import org.hyperic.sigar.Cpu;

/**
 *
 * @author Gof
 */
public class JSBridge {

    private SigarInfo sigar;
    
    public JSBridge() {
        sigar = new SigarInfo();
    }

    public String getRuntimeInfo() {
        JsonObject json = new JsonObject();

        Runtime runtime = Runtime.getRuntime();

        json.addProperty("numProcessors", runtime.availableProcessors());
        json.addProperty("freeMemory", runtime.freeMemory());
        json.addProperty("maxMemory", runtime.maxMemory());
        json.addProperty("totalMemory", runtime.totalMemory());

        return json.toString();
    }
    
    public SigarInfo getSigar() {
        return sigar;
    }
}
