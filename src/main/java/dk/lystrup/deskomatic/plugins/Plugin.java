/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.plugins;

import netscape.javascript.JSObject;

/**
 *
 * @author Gof
 */
public interface Plugin {
    public void registerPluginCallback(String event, JSObject callback);
}
