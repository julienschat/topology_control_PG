package View.Shapes;

import java.awt.*;
import java.awt.geom.Line2D;

public class Edge extends Shape {

    double x1;
    double y1;
    double x2;
    double y2;

    public boolean highlight = false;

    public Edge(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void drawShape(Graphics2D g2d) {
        Line2D line  = new Line2D.Double(x1, y1, x2, y2);
        g2d.draw(line);
    }

    @Override
    public boolean isNear(double x, double y) {
        // TODO: implement
        return false;
    }
}
