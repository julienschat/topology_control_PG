package View.Shapes;

import java.awt.*;

public class Text extends Shape {
    private final String text;
    private final double x;
    private final double y;

    public Text(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawShape(Graphics2D g) {
        g.drawString(text,(float) x, (float) y);
    }

    @Override
    public boolean isNear(double x, double y) {
        return false;
    }
}
