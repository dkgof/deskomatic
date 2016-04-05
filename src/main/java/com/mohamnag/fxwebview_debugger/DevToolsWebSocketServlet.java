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

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class DevToolsWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
        
        private final DevToolsDebuggerServer server;

        public DevToolsWebSocketServlet(DevToolsDebuggerServer server) {
            this.server = server;
        }
        
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(new DevToolsWebSocketCreator(server));
	}
        
        
}