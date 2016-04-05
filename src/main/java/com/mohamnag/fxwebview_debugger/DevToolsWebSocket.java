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

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import java.io.IOException;
import java.text.MessageFormat;

public class DevToolsWebSocket implements WebSocketListener {
	public static final String WEB_SOCKET_ATTR_NAME = "org.javafx.devtools.DevToolsWebSocket"; 
	private Session session;
	private final DevToolsDebuggerServer server;

	public DevToolsWebSocket(DevToolsDebuggerServer server) {
		this.server = server;
	}

	@Override
	public void onWebSocketConnect(Session session) {
		this.session = session;
		if (server.getServletContext().getAttribute(WEB_SOCKET_ATTR_NAME) != null) {
			session.close();
			System.out.println("Another client is already connected. Connection refused");
		} else {
			server.getServletContext().setAttribute(WEB_SOCKET_ATTR_NAME, this);
			System.out.println("Client connected");
		}
	}

	@Override
	public void onWebSocketClose(int closeCode, String message) {
		DevToolsWebSocket mainSocket = (DevToolsWebSocket) server.getServletContext().getAttribute(WEB_SOCKET_ATTR_NAME);
		if (mainSocket == this) {
			server.getServletContext().removeAttribute(WEB_SOCKET_ATTR_NAME);
			System.out.println("Client disconnected");
		}
	}

	public void sendMessage(String data) throws IOException {
		RemoteEndpoint remote = session.getRemote();
		remote.sendString(data);
	}

	@Override
	public void onWebSocketText(String data) {
		server.sendMessageToBrowser(data);
	}
	
	@Override
	public void onWebSocketError(Throwable t) {
		String errorMessage = t.getMessage();
		System.out.println(MessageFormat.format("WebSocket error occurred: {0}", errorMessage));
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
	}

}