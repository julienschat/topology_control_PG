package View.Shapes;

import java.awt.*;
import java.awt.geom.Line2D;

public class Edge extends Shape {

    double x1;
    double y1;
    double x2;
    double y2;

    public boolean highlight = false;
    public int strokeWidth = 1;

    public Edge(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Stroke s = g2d.getStroke();
        if (strokeWidth != 1) {
            g2d.setStroke(new BasicStroke(strokeWidth));
        }
        Line2D line  = new Line2D.Double(x1, y1, x2, y2);
        g2d.draw(line);
        if (strokeWidth != 1) {
            g2d.setStroke(s);
        }
    }

    @Override
    public boolean isNear(double x, double y) {
        // TODO: implement
        return false;
    }
}
