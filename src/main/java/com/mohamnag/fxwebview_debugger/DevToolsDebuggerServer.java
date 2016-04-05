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

import com.sun.javafx.scene.web.Debugger;
import javafx.application.Platform;
import javafx.util.Callback;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletContext;
import java.io.IOException;

public class DevToolsDebuggerServer {
    private ServletContextHandler contextHandler;
    private Debugger debugger;
    private Server server;

    private static int debuggerPort = 4260;
    
    public DevToolsDebuggerServer(Debugger debugger) throws Exception {
        server = new Server(debuggerPort);

        debugger.setEnabled(true);
        debugger.sendMessage("{\"id\" : -1, \"method\" : \"Network.enable\"}");

        contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        ServletHolder devToolsHolder = new ServletHolder(new DevToolsWebSocketServlet(this));
        contextHandler.addServlet(devToolsHolder, "/");

        server.setHandler(contextHandler);
        server.start();

        this.debugger = debugger;
        debugger.setMessageCallback(new Callback<String, Void>() {
            @Override
            public Void call(String data) {
                DevToolsWebSocket mainSocket = (DevToolsWebSocket) contextHandler.getServletContext()
                        .getAttribute(DevToolsWebSocket.WEB_SOCKET_ATTR_NAME);
                if (mainSocket != null) {
                    try {
                        mainSocket.sendMessage(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });

        String remoteUrl = "chrome-devtools://devtools/bundled/inspector.html?ws=localhost:" + debuggerPort + "/";
        System.out.println("To debug open chrome and load next url: " + remoteUrl);
        debuggerPort++;
    }
    
    public void stopDebugServer() throws Exception {
        if (server != null) {
            server.stop();
            server.join();
        }
    }

    public void sendMessageToBrowser(final String data) {
        Platform.runLater(new Runnable() {
            // Display.asyncExec won't be successful here
            @Override
            public void run() {
                debugger.sendMessage(data);
            }
        });
    }

    public String getServerState() {
        return server == null ? null : server.getState();
    }

    public ServletContext getServletContext() {
        return (contextHandler != null) ? contextHandler.getServletContext() : null;
    }
}