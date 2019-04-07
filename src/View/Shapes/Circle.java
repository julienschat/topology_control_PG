package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A circular shape
 */
public class Circle extends Shape {

    private double x, y, radius;

    public Circle(double x, double y, double r) {
        this.x = x;
        this.y = y;
        radius = r;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);

        g2d.fill(circle);
        g2d.draw(circle);
    }

    public boolean isNear(double x, double y) {
        return false;
    }
}
