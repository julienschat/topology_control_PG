package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Draw a node of a graph.
 */
public class Node extends Shape {

    private double x;
    private double y;
    private double radius = 5;
    public boolean drawBorder = false;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void updateCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        g2d.fill(circle);
        if (drawBorder) {
            Ellipse2D.Double border = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
            Color formerColor = g2d.getColor();
            g2d.setColor(Color.black);
            g2d.draw(border);
            g2d.setColor(formerColor);
        }


    }

    @Override
    public boolean isNear(double x, double y) {
        return sqrt(pow(x - this.x, 2) + pow(y - this.y, 2)) < this.radius;
    }
}
