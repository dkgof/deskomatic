/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.cookies;

import java.net.CookieHandler;

/**
 *
 * @author Gof
 */
public class Cookies {
    private final CookieHandler handler;
    
    public Cookies(String cookieStorePath) {
        handler = new MyCookieHandler(cookieStorePath);
        java.net.CookieHandler.setDefault(handler);
    }
}
