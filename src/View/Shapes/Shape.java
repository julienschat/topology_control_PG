package View.Shapes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public abstract class Shape {

    private java.util.List<MouseAdapter> mouseListeners = new ArrayList<MouseAdapter>();

    public abstract void draw(Graphics g);

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
