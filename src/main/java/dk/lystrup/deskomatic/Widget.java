/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

/**
 *
 * @author Gof
 */
public class Widget {

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    private JFrame frame;
    private WebView browser;
    private JFXPanel jfxPanel;
    private final String widgetUrl;
    private final int width;
    private final int height;

    public Widget(String url, int width, int height) {
        this.widgetUrl = url;
        this.width = width;
        this.height = height;

        createWidgetFrame();
    }

    private void createWidgetFrame() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();
            jfxPanel = new JFXPanel();

            frame.setUndecorated(true);
            frame.setSize(width, height);

            frame.setBackground(TRANSPARENT);
            jfxPanel.setBackground(TRANSPARENT);

            setupDragging();

            frame.getContentPane().add(jfxPanel);

            frame.setVisible(true);

            System.out.println("Widget frame created...");

            createBrowser();
        });
    }

    private void createBrowser() {
        Platform.runLater(() -> {
            //Create a new WebView
            browser = new WebView();

            //Add it to our JFXPanel
            jfxPanel.setScene(new Scene(browser, javafx.scene.paint.Color.TRANSPARENT));

            //Setup listeners for events
            WebViewListener listener = new WebViewListener();
            browser.getEngine().documentProperty().addListener(listener);

            browser.getEngine().load(widgetUrl);

            System.out.println("Browser module added...");
        });
    }

    private void makeWebViewTransparent() {
        try {
            // Use reflection to retrieve the WebEngine's private 'page' field. 
            Field pageField = browser.getEngine().getClass().getDeclaredField("page");
            pageField.setAccessible(true);
            com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) pageField.get(browser.getEngine());
            page.setBackgroundColor((new java.awt.Color(0, 0, 0, 0)).getRGB());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Point dragStart;

    private void setupDragging() {
        SwingUtilities.invokeLater(() -> {

            jfxPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
                        dragStart = e.getPoint();
                        
                        e.consume();
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        dragStart = null;
                    }
                }
            });

            jfxPanel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragStart != null && e.isControlDown()) {
                        // get location of Window
                        int thisX = frame.getLocation().x;
                        int thisY = frame.getLocation().y;

                        // Determine how much the mouse moved since the initial click
                        int xMoved = (thisX + e.getX()) - (thisX + dragStart.x);
                        int yMoved = (thisY + e.getY()) - (thisY + dragStart.y);

                        // Move window to this position
                        int X = thisX + xMoved;
                        int Y = thisY + yMoved;
                        frame.setLocation(X, Y);
                        
                        e.consume();
                    }
                }
            });
        });
    }

    private class WebViewListener implements ChangeListener<Document> {

        public WebViewListener() {
        }

        @Override
        public void changed(ObservableValue<? extends Document> ov, Document oldDocument, Document newDocument) {
            System.out.println("Document loaded...");

            //Make sure WebView page is transparent.
            makeWebViewTransparent();
        }
    }
}
