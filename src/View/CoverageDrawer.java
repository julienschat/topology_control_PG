package View;

import Model.Graph;
import View.Shapes.Text;

public class CoverageDrawer {
    public static void drawTextLabels(DrawPanel drawPanel, Graph graph) {
        for (Model.Edge edge : graph.edgeList) {
            drawPanel.shapes.add(new Text("" + edge.coverage, edge.left.x + (edge.right.x - edge.left.x) / 2,edge.left.y + (edge.right.y - edge.left.y) / 2));
        }
    }
}