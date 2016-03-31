/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import java.awt.Color;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;

/**
 *
 * @author Gof
 */
public class Widget {
    private static final Color TRANSPARENT = new Color(0,0,0,0);
    
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

            frame.getContentPane().add(jfxPanel);
            
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
            
            //Make sure WebView page is transparent.
            final com.sun.webkit.WebPage webPage = com.sun.javafx.webkit.Accessor.getPageFor(browser.getEngine());
            webPage.setBackgroundColor(0);
            
            //Setup listeners for events
            WebViewListener listener = new WebViewListener();
            browser.getEngine().documentProperty().addListener(listener);
            
            browser.getEngine().load(widgetUrl);
            
            System.out.println("Browser module added...");
        });
    }

    private void showWidget() {
        SwingUtilities.invokeLater(() -> {
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    private class WebViewListener implements ChangeListener<Document> {
        public WebViewListener() {
        }

        @Override
        public void changed(ObservableValue<? extends Document> ov, Document oldDocument, Document newDocument) {
            System.out.println("Document loaded...");
            
            Widget.this.showWidget();
        }
    }    
}
