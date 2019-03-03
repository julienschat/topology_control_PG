package View;

import View.Shapes.Shape;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;

public class DrawPanel extends JPanel{

    public java.util.List<View.Shapes.Shape> shapes = new LinkedList<View.Shapes.Shape>();
    private int i = 0;

    @FunctionalInterface
    private interface RedirectMouseEvent {
        void redirect(MouseAdapter ma);
    }

    public DrawPanel(){
        this.attachMouseListener();
        this.setBackground(Color.CYAN);
    }

    public void registerResize(JPanel mainPanel) {
        DrawPanel drawPanel = this;
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                drawPanel.getLayout().preferredLayoutSize(mainPanel);
                System.out.println("resize");
            }
        });
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


    public void drawCircle(Graphics g, int x, int y,int radius){
        Ellipse2D.Double circle = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fill(circle);
        //g.drawOval(x,y,radius,radius);

    }

    public void drawEdge(Graphics g,int x1,int y1, int x2, int y2){
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawLine(x1,y1,x2,y2);
    }

}
