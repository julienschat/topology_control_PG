package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class DrawPanel extends JPanel{

    private int testX;
    private int testY;
    public java.util.List<Shape> shapes;
    private int i = 0;

    public DrawPanel(){
        this.attachMouseListener();
    }

    private void attachMouseListener() {
        DrawPanel panel = this;
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                panel.testX = e.getX();
                panel.testY = e.getY();
                panel.revalidate();
                panel.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        };

        this.addMouseListener(adapter);
        this.addMouseMotionListener(adapter);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //for (Shape shape : shapes){
        //     shape.draw(g);
        //}

        drawCircle(g,20+i,20,10);
        drawCircle(g,40,40,10);
        drawEdge(g,20,20,40,40);

        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        i++;
        drawCircle(g,this.testX,this.testY,10);
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
