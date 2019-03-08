package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Circle extends Shape {

    public double x,y,radius;
    public Circle(double x, double y, double r) {
        this.x = x;
        this.y = y;
        radius = r;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        //Ellipse2D.Double border = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);


        g2d.fill(circle);
        g2d.draw(circle);
        //Color before = this.color;
        //g2d.setColor(color.black);
        //g2d.draw(border);
        //g2d.setColor(before);
    }

    public boolean isNear(double x,double y){
        return false;
    }
}
