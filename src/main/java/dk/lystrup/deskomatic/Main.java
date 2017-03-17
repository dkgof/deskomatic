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

        String sigarDisk = new File("./widgets/SigarDisk/index.html").toURI().toString();
        Widget sigarDiskWidget = new Widget(sigarDisk, 285, 565);

        String sigarCpu = new File("./widgets/SigarCpu/index.html").toURI().toString();
        Widget sigarCpuWidget = new Widget(sigarCpu, 285, 250);


        String sigarNet = new File("./widgets/SigarNet/index.html").toURI().toString();
        Widget sigarNetWidget = new Widget(sigarNet, 285 * 2, 255);

        String sigarMem = new File("./widgets/SigarMem/index.html").toURI().toString();
        Widget sigarMemWidget = new Widget(sigarMem, 285, 180);

        String google = "http://google.com/";
        Browser googleWidget = new Browser(google, 1024, 768);
        
        while(true) {
            try {
                Thread.sleep(10000);
            } catch(Exception e) {
                
            }
        }
    }
}
