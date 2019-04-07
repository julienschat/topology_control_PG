package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

/**
 * A circle around a point
 */
public class Radius extends Shape {
    public double x;
    public double y;
    private double radius;

    public Radius(double x, double y, double r) {
        this.x = x;
        this.y = y;
        radius = r;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        g2d.draw(circle);
    }

    @Override
    public boolean isNear(double x, double y) {
        double dist = sqrt(pow(x - this.x, 2) + pow(y - this.y, 2));
        return dist >= radius - 2 && dist <= radius + 2;
    }
}
