/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic.widget;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author Gof
 */
public class Browser {

    private JFrame frame;
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

    public Browser(String url, int width, int height) {
        this.widgetUrl = url;
        this.width = width;
        this.height = height;

        createWidgetFrame();
    }

    private void createWidgetFrame() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Browser");
            jfxPanel = new JFXPanel();

            frame.setSize(width, height);

            frame.setLayout(new BorderLayout());
            frame.getContentPane().add(jfxPanel, BorderLayout.CENTER);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JPanel locationPanel = new JPanel();
            locationPanel.setLayout(new GridBagLayout());
            
            JTextField location = new JTextField();
            locationPanel.add(location, gbc);
            
            frame.getContentPane().add(locationPanel, BorderLayout.NORTH);
            
            JButton browse = new JButton("Go!");
            browse.addActionListener((actionEvent) -> {
                Platform.runLater(() -> {
                    if(browser != null) {
                        System.out.println("Loading: "+location.getText());
                        String url = location.getText();
                        
                        if(!url.startsWith("http")) {
                            url = "http://" + url;
                        }
                        
                        browser.getEngine().load(url);
                    }
                });
            });
            locationPanel.add(browse);
            
            JPanel historyPanel = new JPanel();
            historyPanel.setLayout(new GridLayout(1,2));
            
            JButton forward = new JButton("Forward");
            forward.addActionListener((action) -> {
                Platform.runLater(() -> {
                    if(browser != null) {
                        browser.getEngine().executeScript("window.history.forward()");
                    }
                });
            });
            
            JButton back = new JButton("Back");
            back.addActionListener((action) -> {
                Platform.runLater(() -> {
                    if(browser != null) {
                        browser.getEngine().executeScript("window.history.back()");
                    }
                });
            });
            
            historyPanel.add(back, BorderLayout.SOUTH);
            historyPanel.add(forward, BorderLayout.SOUTH);
            
            frame.getContentPane().add(historyPanel, BorderLayout.SOUTH);
            
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

            browser.getEngine().load(widgetUrl);
            
            System.out.println("Browser module added...");
        });
    }
}
