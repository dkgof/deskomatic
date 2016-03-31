/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import java.io.File;

/**
 *
 * @author Gof
 */
public class Main {
    public static void main(String[] args) {
        File sysInfoFile = new File("C:\\Users\\Gof\\Documents\\DeskOMatic\\SysInfo\\index.html");
        
        Widget w = new Widget(sysInfoFile.toURI().toString(), 512, 512);
    }
}
