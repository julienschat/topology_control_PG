package View;

import Model.Graph;
import View.Shapes.Edge;
import View.Shapes.Radius;
import View.Shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Observable;

public class GraphDrawer extends Observable {
    private DrawPanel drawPanel;

    private boolean nodeClick = false;

    public Model.Node draggedNode;
    public Model.Node hoveredNode;
    public Model.Node clickedNode;
    public Model.Node draggedRadiusNode;
    private boolean drawHeatMap = false;

    public GraphDrawer(DrawPanel panel) {
        this.drawPanel = panel;
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (nodeClick) {
                    nodeClick = false;
                } else {
                    clickedNode = null;
                }
            }
        });
    }

    public void drawNode(Model.Node modelNode, boolean drawRadius, Color color) {
        View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x, modelNode.y);
        viewNode.color = color;

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
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clickedNode = modelNode;
                nodeClick = true;
            }
        });

        drawPanel.shapes.add(viewNode);

        if (drawRadius) {
            View.Shapes.Radius radius = new Radius(modelNode.x, modelNode.y, modelNode.radius);
            radius.color = color;

            radius.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    radius.color = Color.RED;
                    drawPanel.update();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    radius.color = Color.BLACK;
                    drawPanel.update();
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

            drawPanel.shapes.add(radius);
        }
    }

    private void updateMap(int[][] map, int gridSize, Model.Edge edge) {
        double radius = edge.getLength();
        double radiusSq = Math.pow(radius, 2);
        int gridBoxLeft = Math.max(0, (int)(Math.min(edge.left.x, edge.right.x) - radius) / gridSize);
        int gridBoxTop =  Math.max(0, (int)(Math.min(edge.left.y, edge.right.y) - radius) / gridSize);
        int gridRadius = (int)radius / gridSize;
        for (int x = gridBoxLeft; x < Math.min(gridBoxLeft + 3 * gridRadius, map.length); x++) {
            double distXLeftSq = Math.pow((x + 0.5) * gridSize - edge.left.x, 2);
            double distXRightSq = Math.pow((x + 0.5) * gridSize - edge.right.x, 2);
            for (int y = gridBoxTop; y < Math.min(gridBoxTop + 3 * gridRadius, map[x].length); y++) {
                double distYLeftSq = Math.pow((y + 0.5) * gridSize - edge.left.y, 2);
                double distYRightSq = Math.pow((y + 0.5) * gridSize - edge.right.y, 2);
                if (distXLeftSq + distYLeftSq < radiusSq || distXRightSq + distYRightSq < radiusSq) {
                    map[x][y] += 1;
                }
            }
        }
    }

    public void drawHeatMap(Graph graph){
        int height = drawPanel.getHeight();
        int width = drawPanel.getWidth();
        int gridSize = 2;

        int[][] map = new int[width / gridSize + 1][height / gridSize + 1];
        for (Model.Edge edge: graph.edgeList) {
            updateMap(map, gridSize, edge);
        }
        // possibility 1
        int scaling = Arrays.stream(map).map(row -> Arrays.stream(row).max().orElse(0)).max(Comparator.naturalOrder()).orElse(0);
        // possibility 2
//        int scaling = graph.nodeList.size() * 2;

        for (int i = 0; i < width / gridSize + 1; i++) {
            for (int j = 0; j < height / gridSize + 1; j ++) {
                if (map[i][j] > 0) {
                    View.Shapes.Rectangle viewRect = new Rectangle(i * gridSize, j * gridSize, gridSize, gridSize);
                    float heatValue = ((float) map[i][j] / scaling);
                    float hue = 0.6f - (heatValue * 0.6f);
                    viewRect.color = new Color(Color.HSBtoRGB(hue, 1, 0.5f));
                    drawPanel.shapes.add(viewRect);
                }
            }
        }

//        for (int i = 0; i < width; i += gridSize) {
//            for (int j = 0; j < height; j += gridSize) {
//                int centerX = i + gridSize / 2;
//                int centerY = j + gridSize / 2;
//                long count = graph.edgeList.stream()
//                        .filter(e -> e.checkIfPointIsCovered(centerX, centerY))
//                        .count();
//                if (count > 0) {
//                    View.Shapes.Rectangle viewRect = new Rectangle(i, j, gridSize, gridSize);
//                    float heatValue = ((float) count / maxCoverage);
//                    float hue = 0.6f - (heatValue * 0.6f);
//                    viewRect.color = new Color(Color.HSBtoRGB(hue, 1, 0.5f));
//                    drawPanel.shapes.add(viewRect);
//                }
//            }
//        }
    }

    public void draw(Graph graph, boolean radii, boolean heatMap) {
        drawPanel.shapes.clear(); // NOTE: should maybe only delete own shapes.

        if(heatMap) {
            drawHeatMap(graph);
        }

        for (Model.Node modelNode : graph.nodeList) {
            drawNode(modelNode, radii, Color.BLACK);
//            double dist = node.edgeList.stream()
//                    .map(e -> e.left.distanceTo(e.right))
//                    .max(Comparator.naturalOrder())
//                    .orElse((double) 0);
//            if (dist > 0) {
//                drawPanel.shapes.add(new Radius(node.x, node.y, dist));
//            }
//            for (Model.Node other : graph.nodeList) {
//                if (node.distanceTo(other) < dist) {
//                    if (!node.getNeighbours().contains(other)) {
//                        Edge shapeEdge = new Edge(node.x, node.y, other.x, other.y);
//                        shapeEdge.highlight = true;
//                        drawPanel.shapes.add(shapeEdge);
//                    }
//                }
//            }
        }
        for (Model.Edge edge: graph.edgeList) {
            drawPanel.shapes.add(new Edge(edge.left.x, edge.left.y, edge.right.x, edge.right.y));
        }
        drawPanel.update();
    }
}
