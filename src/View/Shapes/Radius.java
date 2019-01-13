package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Radius extends Shape {
    public double x;
    public double y;
    public double radius;

    public Radius(double x, double y, double r) {
        this.x = x;
        this.y = y;
        radius = r;
    }

    @Override
    public void draw(Graphics g) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        g2d.draw(circle);
        g2d.setColor(Color.BLACK);
    }

    @Override
    public boolean isNear(double x, double y) {
        return false;
    }
}
