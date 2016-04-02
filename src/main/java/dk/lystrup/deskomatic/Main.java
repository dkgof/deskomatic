/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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

        Widget runtimeInfoWidget = new Widget(runtimeInfo, 256, 200);
    }
}
