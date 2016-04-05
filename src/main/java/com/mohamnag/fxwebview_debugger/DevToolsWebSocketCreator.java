/**
 * Originally mentioned on Stack overflow:
 * http://stackoverflow.com/questions/9398879/html-javascript-debugging-in-javafx-webview/34444807#34444807
 * 
 * Rewritten by Mohammad Naghavi
 * 
 * https://github.com/mohamnag/javafx_webview_debugger
 * 
 * Non static version by Rolf Bagge
 */

package com.mohamnag.fxwebview_debugger;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class DevToolsWebSocketCreator implements WebSocketCreator {
 
    private final DevToolsDebuggerServer server;
    
    public DevToolsWebSocketCreator(DevToolsDebuggerServer server) {
        this.server = server;
    }
 
    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return new DevToolsWebSocket(server);
    }
}