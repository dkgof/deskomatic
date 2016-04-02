/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template fdiskdsdssdsdile, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import dk.lystrup.deskomatic.widget.Widget;
import dk.lystrup.deskomatic.cookies.Cookies;
import java.io.File;

/**
 *
 * @author Gof
 */
public class Main {
    public static void main(String[] args) {
        //Setup persistent cookie storing
        Cookies cookies = new Cookies("./cookies.json");
        
        //JavaRuntime info
        String runtimeInfo = new File("./widgets/RuntimeInfo/index.html").toURI().toString();
        Widget runtimeInfoWidget = new Widget(runtimeInfo, 220, 200);

        String sigarCpu = new File("./widgets/SigarCpu/index.html").toURI().toString();
        Widget sigarCpuWidget = new Widget(sigarCpu, 220, 300);

        String sigarDisk = new File("./widgets/SigarDisk/index.html").toURI().toString();
        Widget sigarDiskWidget = new Widget(sigarDisk, 220, 550);
    }
}
