/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

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
        
        //Calendar view widget
        String calendarView = new File("C:\\Users\\Gof\\Documents\\DeskOMatic\\SysInfo\\index.html").toURI().toString();
        
        //Google login page
        String googleLogin = "https://accounts.google.com";
        
        Widget w = new Widget(calendarView, 512, 512);
        //Widget w2 = new Widget(calendarView, 512, 512);
    }
}
