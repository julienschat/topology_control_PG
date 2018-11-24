package View;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DrawPanel extends JPanel{

    public DrawPanel(){

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        drawCircle(g,20,20,10);
        drawCircle(g,40,40,10);
        drawEdge(g,20,20,40,40);

        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();


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
