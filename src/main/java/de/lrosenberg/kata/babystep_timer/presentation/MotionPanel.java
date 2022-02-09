package de.lrosenberg.kata.babystep_timer.presentation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class MotionPanel extends JPanel {
    
    private java.awt.Point initialClick;
    private JFrame parent;

    public MotionPanel(final JFrame parent){
    this.parent = parent;

    addMouseListener(new MouseInputAdapter() {
        public void mousePressed(MouseEvent e) {
            initialClick = e.getPoint();
            getComponentAt(initialClick);
        }
    });

    addMouseMotionListener(new MouseMotionListener() {

        @Override
        public void mouseDragged(MouseEvent e) {

            // get location of Window
            int thisX = parent.getLocation().x;
            int thisY = parent.getLocation().y;

            // Determine how much the mouse moved since the initial click
            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;

            // Move window to this position
            int X = thisX + xMoved;
            int Y = thisY + yMoved;
            parent.setLocation(X, Y);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
        }
    });
    }
}