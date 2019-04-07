package View.Shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * A rectangular shape
 */
public class Rectangle extends Shape {
    private double x, y, height, width;

    public Rectangle(double x, double y, double height, double width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Rectangle2D.Double rect = new Rectangle2D.Double(this.x, this.y, this.width, this.height);
        g2d.fill(rect);
        g2d.draw(rect);
    }

    @Override
    public boolean isNear(double x, double y) {
        return false;
    }
}
