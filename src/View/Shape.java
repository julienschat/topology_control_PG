package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public abstract class Shape {

    public boolean acceptAllEvents = false;

    private java.util.List<MouseAdapter> mouseListeners = new ArrayList<MouseAdapter>();

    public abstract void draw(Graphics g);

    public abstract boolean isNear(int x, int y);

    public boolean shouldHandleMouseEventAt(int x, int y) {
        return this.acceptAllEvents || this.isNear(x, y);
    }

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
