/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import netscape.javascript.JSObject;

/**
 *
 * @author Gof
 */
public abstract class AbstractPlugin implements Plugin {

    private final Set<String> allowedEvents = new HashSet<>();
    
    private final HashMap<String, List<JSObject>> callbacks = new HashMap<>();
    
    public void addAllowedEvent(String event) {
        allowedEvents.add(event);
    }
    
    @Override
    public void registerPluginCallback(String event, JSObject callback) {
        if(allowedEvents.contains(event)) {
            List<JSObject> callbackList = callbacks.get(event);
            if(callbackList == null) {
                callbackList = new LinkedList<>();
                callbacks.put(event, callbackList);
            }

            callbackList.add(callback);
        } else {
            throw new IllegalArgumentException("No event with name ["+event+"] on plugin: "+this.getClass().getName());
        }
    }
    
    public void callCallback(String event, Object... parameters) {
        Iterator<JSObject> it = callbacks.get(event).iterator();
        while(it.hasNext()) {
            JSObject callback = it.next();
            try {
                callback.call("call", "", parameters);
            } catch(Exception e) {
                System.out.println("Error in callback, unregistering!");
                it.remove();
            }
        }
    }
}
