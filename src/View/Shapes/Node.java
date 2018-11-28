package View.Shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Node extends Shape {

    int x;
    int y;
    int radius = 5;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        Ellipse2D.Double circle = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fill(circle);
    }

    @Override
    public boolean isNear(int x, int y) {
        return sqrt(pow(x - this.x, 2) + pow(y - this.y, 2)) < this.radius;
    }
}
