/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.widget;

import com.sun.javafx.webkit.Accessor;
import dk.lystrup.deskomatic.jsinterop.JSBridge;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

/**
 *
 * @author Gof
 */
public class Widget {

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final Color DRAG_COLOR = new Color(0, 0, 0, 128);
    private static final Color WINDOW_INFO_COLOR = new Color(255, 255, 255, 95);

    private JWindow window;
    private WebView browser;
    private JFXPanel jfxPanel;

    private JWindow windowInfo;
    private JLabel resolutionLabel;
    private JLabel positionLabel;
    
    private final String widgetUrl;
    private final int width;
    private final int height;

    private Point dragStart;
    private Point resizeLast;

    public Widget(String url, int width, int height) {
        this.widgetUrl = url;
        this.width = width;
        this.height = height;

        createWidgetFrame();
    }

    private void createWidgetFrame() {
        SwingUtilities.invokeLater(() -> {
            window = new JWindow();
            jfxPanel = new JFXPanel();

            window.setSize(width, height);

            window.setBackground(TRANSPARENT);
            jfxPanel.setBackground(TRANSPARENT);

            setupDragging();

            window.getContentPane().add(jfxPanel);

            window.setVisible(true);

            windowInfo = new JWindow();
            windowInfo.getContentPane().setLayout(new GridLayout(2, 1));
            windowInfo.setBackground(WINDOW_INFO_COLOR);
            
            resolutionLabel = new JLabel(window.getWidth()+"x"+window.getHeight());
            positionLabel = new JLabel(window.getX()+", "+window.getY());
            resolutionLabel.setBackground(WINDOW_INFO_COLOR);
            positionLabel.setBackground(WINDOW_INFO_COLOR);
            
            windowInfo.getContentPane().add(resolutionLabel);
            windowInfo.getContentPane().add(positionLabel);
            
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
            browser.getEngine().documentProperty().addListener((ObservableValue<? extends Document> ov, Document oldDocument, Document newDocument) -> {
                System.out.println("Document loaded...");
                
                JSObject jsWindow = (JSObject) browser.getEngine().executeScript("window");
                jsWindow.setMember("deskomatic", new JSBridge());
                browser.getEngine().executeScript("jsBridgeReady()");
            });
            
            browser.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> ov, State oldState, State newState) -> {
                if(newState == State.SUCCEEDED) {
                    System.out.println("Load page complete...");
                }
            });

            browser.getEngine().setCreatePopupHandler((PopupFeatures config) -> {
                System.out.println("Popup requested: "+config.toString());
                return null;
            });

            browser.getEngine().load(widgetUrl);
            
            //Make background page transparent
            Accessor.getPageFor(browser.getEngine()).setBackgroundColor(0);

            System.out.println("Browser module added...");
        });
    }

    private void destroy() {
        window.setVisible(false);
        window.dispose();
        browser = null;
        window = null;
        jfxPanel = null;
    }
    
    private void showWindowInfo() {
        windowInfo.pack();
        windowInfo.setVisible(true);
        windowInfo.setLocation(window.getLocationOnScreen());
    }

    private void updateWindowInfo() {
        resolutionLabel.setText(window.getWidth()+"x"+window.getHeight());
        positionLabel.setText(window.getX()+", "+window.getY());
        windowInfo.pack();
        windowInfo.setLocation(window.getLocationOnScreen());
    }

    private void hideWindowInfo() {
        windowInfo.setVisible(false);
    }
    
    private void setupDragging() {
        SwingUtilities.invokeLater(() -> {

            jfxPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
                        dragStart = e.getPoint();

                        showWindowInfo();
                        
                        e.consume();
                    }
                    if (e.getButton() == MouseEvent.BUTTON1 && e.isAltDown()) {
                        resizeLast = e.getPoint();

                        window.setBackground(DRAG_COLOR);
                        
                        showWindowInfo();

                        e.consume();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if(resizeLast != null) {
                            window.setBackground(TRANSPARENT);
                        }
                        
                        hideWindowInfo();
                        
                        dragStart = null;
                        resizeLast = null;
                    }
                }
            });

            jfxPanel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragStart != null && e.isControlDown()) {
                        // get location of Window
                        int thisX = window.getLocation().x;
                        int thisY = window.getLocation().y;

                        // Determine how much the mouse moved since the initial click
                        int xMoved = (thisX + e.getX()) - (thisX + dragStart.x);
                        int yMoved = (thisY + e.getY()) - (thisY + dragStart.y);

                        // Move window to this position
                        int X = thisX + xMoved;
                        int Y = thisY + yMoved;
                        window.setLocation(X, Y);

                        updateWindowInfo();
                        
                        e.consume();
                    }
                    
                    if(resizeLast != null && e.isAltDown()) {
                        int dragX = e.getX() - resizeLast.x;
                        int dragY = e.getY() - resizeLast.y;
                        
                        Dimension size = window.getSize();
                        size.setSize(size.getWidth() + dragX, size.getHeight()  + dragY);
                        window.setSize(size);
                        
                        resizeLast = e.getPoint();
                        
                        updateWindowInfo();
                        
                        e.consume();
                    }
                }
            });
        });
    }
}
