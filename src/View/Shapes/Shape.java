package View.Shapes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

/**
 * Abstract base class for draw instructions. Providing mouselistener interfaces which get fired by the draw panel,
 * these rely on the abstract isNear function.
 */
public abstract class Shape {
    public Color color = Color.BLACK;
    public boolean hovered = false;

    private java.util.List<MouseAdapter> mouseListeners = new ArrayList<MouseAdapter>();

    public void draw(Graphics2D g2d) {
        Color before = g2d.getColor();
        g2d.setColor(color);
        drawShape(g2d);
        g2d.setColor(before);
    }

    public abstract void drawShape(Graphics2D g);

    public abstract boolean isNear(double x, double y);

    public void addMouseListener(MouseAdapter listener) {
        this.mouseListeners.add(listener);
    }

    public void removeMouseListener(MouseAdapter listener) {
        this.mouseListeners.remove(listener);
    }

    public java.util.List<MouseAdapter> getMouseListeners() {
        return this.mouseListeners;
    }
}
