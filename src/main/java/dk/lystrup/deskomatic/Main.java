/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template fdiskdsdssdsdile, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import dk.lystrup.deskomatic.widget.Widget;
import dk.lystrup.deskomatic.cookies.Cookies;
import dk.lystrup.deskomatic.widget.Browser;
import java.io.File;
import oshi.SystemInfo;

/**
 *
 * @author Gof
 */
public class Main {
    public static void main(String[] args) {
        //Setup persistent cookie storing
        Cookies cookies = new Cookies("./cookies.json");
        
        String runtimeInfo = new File("./widgets/RuntimeInfo/index.html").toURI().toString();
        Widget runtimeInfoWidget = new Widget(runtimeInfo, 285, 210);

        String cpuInfo = new File("./widgets/CPUInfo/index.html").toURI().toString();
        Widget cpuInfoWidget = new Widget(cpuInfo, 285, 210);

        String google = "http://google.com/";
        //Browser googleWidget = new Browser(google, 1024, 768);
        
        while(true) {
            try {
                Thread.sleep(10000);
            } catch(Exception e) {
                
            }
        }
    }
}
