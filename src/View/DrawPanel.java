package View;

import View.Shapes.Shape;

import javax.swing.*;
import java.awt.*;
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

    private void update() {
        this.revalidate();
        this.repaint();
    }

    private void attachMouseListener() {
        DrawPanel panel = this;
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseClicked(e));
                panel.update();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                panel.redirectMouseEvent(e, ma -> ma.mousePressed(e));
                panel.update();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseReleased(e));
                panel.update();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseEntered(e));
                panel.update();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseExited(e));
                panel.update();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseDragged(e));
                panel.update();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.redirectMouseEvent(e, ma -> ma.mouseMoved(e));
                panel.update();
            }
        };

        this.addMouseListener(adapter);
        this.addMouseMotionListener(adapter);
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
