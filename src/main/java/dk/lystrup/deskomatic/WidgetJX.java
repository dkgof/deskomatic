/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lystrup.deskomatic;

import com.sun.javafx.webkit.Accessor;
import java.awt.Color;
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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;

/**
 *
 * @author Gof
 */
public class WidgetJX {

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    private JFrame frame;
    private WebView browser;
    private JFXPanel jfxPanel;

    private final String widgetUrl;
    private final int width;
    private final int height;

    private Point dragStart;

    public WidgetJX(String url, int width, int height) {
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
    }

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
}
