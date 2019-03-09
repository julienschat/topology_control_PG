package View;

import View.Shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class DrawPanel extends JPanel {

    public java.util.List<View.Shapes.Shape> shapes = new LinkedList<View.Shapes.Shape>();

    @FunctionalInterface
    private interface RedirectMouseEvent {
        void redirect(MouseAdapter ma);
    }

    public DrawPanel() {
        this.attachMouseListener();
    }

    private void redirectMouseEvent(MouseEvent e, RedirectMouseEvent deferrer) {
        for (View.Shapes.Shape shape: this.shapes) {
            if (shape.isNear(e.getX(), e.getY())) {
                for (MouseAdapter ma : shape.getMouseListeners()) {
                    deferrer.redirect(ma);
                }
            }
        }
    }

    private void createEnterEvents(MouseEvent e) {
        for (View.Shapes.Shape shape: this.shapes) {
            if (shape.isNear(e.getX(), e.getY()) && !shape.hovered) {
                shape.hovered = true;
                for (MouseAdapter ma : shape.getMouseListeners()) {
                    ma.mouseEntered(e);
                }
            }
        }
    }

    private void createExitEvents(MouseEvent e) {
        for (View.Shapes.Shape shape: this.shapes) {
            if (!shape.isNear(e.getX(), e.getY()) && shape.hovered) {
                shape.hovered = false;
                for (MouseAdapter ma : shape.getMouseListeners()) {
                    ma.mouseExited(e);
                }
            }
        }
    }

    public void update() {
        this.revalidate();
        this.repaint();
    }

    private void attachMouseListener() {
        DrawPanel panel = this;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseClicked(e));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                panel.redirectMouseEvent(e, ma -> ma.mousePressed(e));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseReleased(e));
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseDragged(e));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.createEnterEvents(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseMoved(e));
                panel.createExitEvents(e);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for (Shape shape : shapes){
             shape.draw(g);
        }
    }
}
