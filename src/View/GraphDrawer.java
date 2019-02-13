package View;

import Model.Graph;
import View.Shapes.Edge;
import View.Shapes.Node;
import View.Shapes.Radius;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Observable;

public class GraphDrawer extends Observable {
    private DrawPanel panel;

    public Model.Node draggedNode;
    public Model.Node hoveredNode;
    public Model.Node draggedRadiusNode;

    public GraphDrawer(DrawPanel panel) {
        this.panel = panel;
    }

    public void draw(Graph graph, boolean radii) {
        panel.shapes.clear(); // NOTE: should maybe only delete own shapes.
        for (Model.Node modelNode : graph.nodeList) {
            View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x, modelNode.y);

            viewNode.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    draggedNode = modelNode;
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    draggedNode = null;
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    hoveredNode = modelNode;
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    hoveredNode = null;
                }
            });

            panel.shapes.add(viewNode);

            if (radii) {
                View.Shapes.Radius radius = new Radius(modelNode.x, modelNode.y, modelNode.radius);
                panel.shapes.add(radius);
                radius.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        radius.color = Color.RED;
                        panel.update();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        radius.color = Color.BLACK;
                        panel.update();
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        draggedRadiusNode = modelNode;
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        draggedRadiusNode = null;
                    }
                });
            }

//            double dist = node.edgeList.stream()
//                    .map(e -> e.left.distanceTo(e.right))
//                    .max(Comparator.naturalOrder())
//                    .orElse((double) 0);
//            if (dist > 0) {
//                panel.shapes.add(new Radius(node.x, node.y, dist));
//            }
//            for (Model.Node other : graph.nodeList) {
//                if (node.distanceTo(other) < dist) {
//                    if (!node.getNeighbours().contains(other)) {
//                        Edge shapeEdge = new Edge(node.x, node.y, other.x, other.y);
//                        shapeEdge.highlight = true;
//                        panel.shapes.add(shapeEdge);
//                    }
//                }
//            }
        }
        for (Model.Edge edge: graph.edgeList) {
            panel.shapes.add(new Edge(edge.left.x, edge.left.y, edge.right.x, edge.right.y));
        }
        panel.update();
    }
}
