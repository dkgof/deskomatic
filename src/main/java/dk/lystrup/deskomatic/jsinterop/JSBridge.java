/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.jsinterop;

import dk.lystrup.deskomatic.plugins.sigar.SigarInfo;
import com.google.gson.JsonObject;

/**
 *
 * @author Gof
 */
public class JSBridge {

    public JSBridge() {
    }

    public String getRuntimeInfo() {
        JsonObject json = new JsonObject();

        Runtime runtime = Runtime.getRuntime();

        double freeMemory = runtime.freeMemory() / 1024.0 / 1024.0;
        double totalMemory = runtime.totalMemory() / 1024.0 / 1024.0;
        
        json.add("numProcessors", JSBridge.getJsonWithUnits(runtime.availableProcessors(), null, "# CPU's", 0));
        json.add("freeMemory", JSBridge.getJsonWithUnits(freeMemory, "MB", "Free memory", 1));
        json.add("maxMemory", JSBridge.getJsonWithUnits(runtime.maxMemory() / 1024.0 / 1024.0, "MB", "Max memory", 1));
        json.add("totalMemory", JSBridge.getJsonWithUnits(totalMemory, "MB", "Total memory", 1));
        json.add("usedMemory", JSBridge.getJsonWithUnits(totalMemory - freeMemory, "MB", "Used memory", 1));

        return json.toString();
    }

    public SigarInfo getSigar() {
        return SigarInfo.instance();
    }

    public static JsonObject getJsonWithUnits(Object value, String unit, String description, int fixedDecimals) {
        JsonObject json = new JsonObject();

        if (value instanceof Number) {
            json.addProperty("value", (Number) value);
        } else if (value instanceof Boolean) {
            json.addProperty("value", (Boolean) value);
        } else if (value instanceof String) {
            json.addProperty("value", (String) value);
        } else if (value instanceof Character) {
            json.addProperty("value", (Character) value);
        }

        json.addProperty("unit", unit);
        json.addProperty("description", description);
        json.addProperty("fixedDecimals", fixedDecimals);

        return json;
    }
}
