package View;

import Model.Graph;
import View.Shapes.Edge;
import View.Shapes.Node;
import View.Shapes.Radius;

import java.util.Comparator;

public class GraphDrawer {
    private DrawPanel panel;

    public GraphDrawer(DrawPanel panel) {
        this.panel = panel;
    }

    public void draw(Graph graph) {
        panel.shapes.clear();
        for (Model.Node node : graph.nodeList) {
            panel.shapes.add(new Node(node.x, node.y));
            double dist = node.edgeList.stream()
                    .map(e -> e.left.distanceTo(e.right))
                    .max(Comparator.naturalOrder())
                    .orElse((double) 0);
            if (dist > 0) {
                panel.shapes.add(new Radius(node.x, node.y, dist));
            }
            for (Model.Node other : graph.nodeList) {
                if (node.distanceTo(other) < dist) {
                    if (!node.getNeighbours().contains(other)) {
                        Edge shapeEdge = new Edge(node.x, node.y, other.x, other.y);
                        shapeEdge.highlight = true;
                        panel.shapes.add(shapeEdge);
                    }
                }
            }
        }
        for (Model.Edge edge: graph.edgeList) {
            panel.shapes.add(new Edge(edge.left.x, edge.left.y, edge.right.x, edge.right.y));
        }
        panel.update();
    }
}
