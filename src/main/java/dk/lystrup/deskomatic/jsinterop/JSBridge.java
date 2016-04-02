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

/**
 *
 * @author Gof
 */
public class JSBridge {

    public JSBridge() {
        ThreadMXBean newBean = ManagementFactory.getThreadMXBean();
        try {
            if (newBean.isThreadCpuTimeSupported()) {
                newBean.setThreadCpuTimeEnabled(true);
            } else {
                throw new AccessControlException("");
            }
        } catch (AccessControlException e) {
            System.out.println("CPU Usage monitoring is not available!");
        }
    }

    public String getRuntimeInfo() {
        JsonObject json = new JsonObject();

        Runtime runtime = Runtime.getRuntime();

        json.addProperty("numProcessors", runtime.availableProcessors());
        json.addProperty("freeMemory", runtime.freeMemory());
        json.addProperty("maxMemory", runtime.maxMemory());
        json.addProperty("totalMemory", runtime.totalMemory());

        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();

        if(threadMX.isThreadCpuTimeEnabled()) {
            long userTime = 0;
            long cpuTime = 0;

            for (long id : threadMX.getAllThreadIds()) {
                userTime += threadMX.getThreadUserTime(id);
                cpuTime += threadMX.getThreadCpuTime(id);
            }

            json.addProperty("cpuTime", cpuTime);
            json.addProperty("userTime", userTime);
        }

        return json.toString();
    }
}
