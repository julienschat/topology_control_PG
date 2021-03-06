package View;

import DataStructures.Edge;
import DataStructures.Node;
import View.Shapes.Rectangle;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Draws a heatmap on the background of a drawPanel. The heatmap shows areas that have high interference.
 * It samples squares in size of the internal gridSize parameter and draws a color ranging from blue over green and
 * yellow to red. Where blue is a minimal interference and red is the maximum.
 */
public class HeatmapDrawer {
    private DrawPanel drawPanel;
    private int[][] map;
    private int gridSize;
    private int scaling;

    public HeatmapDrawer(DrawPanel panel) {
        this.drawPanel = panel;
        this.gridSize = 2;
    }

    public void updateScaling(List<Edge> edgeList) {
        updateScaling(this.calculateMap(gridSize, edgeList));
    }

    private void updateScaling(int[][] map) {
        scaling = Arrays.stream(map)
                .map(row -> Arrays.stream(row).max().orElse(0))
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    private int[][] calculateMap(int gridSize, List<Edge> edgeList) {
        int height = drawPanel.getHeight();
        int width = drawPanel.getWidth();
        // map stores how many edges affect the grid cell
        map = new int[width / gridSize + 1][height / gridSize + 1];
        for (Edge edge : edgeList) {
            updateMapCells(gridSize, edge);
        }

        return map;
    }

    public void scaleAndDrawMap(List<Edge> edgeList) {
        int[][] map = calculateMap(gridSize, edgeList);
        updateScaling(map);
        draw(map);
    }

    private void updateMapCells(int gridSize, Edge edge) {
        // check which grid cells are affected by the edge
        double radius = edge.getLength();
        double radiusSq = Math.pow(radius, 2);
        int boxLeft = Math.max(0, (int) (Math.min(edge.left.x, edge.right.x) - radius) / gridSize);
        int boxTop = Math.max(0, (int) (Math.min(edge.left.y, edge.right.y) - radius) / gridSize);
        int maxBoxSize = 3 * (int) radius / gridSize;
        int boxXSize = Math.min(boxLeft + maxBoxSize, map.length) - boxLeft;
        int boxYSize = Math.min(boxTop + maxBoxSize, map[0].length) - boxTop;

        for (int i = 0; i < boxXSize; i++) {
            double x = (boxLeft + i) * gridSize + gridSize / 2d;
            double distXLeft = Math.pow(x - edge.left.x, 2);
            double distXRight = Math.pow(x - edge.right.x, 2);
            for (int j = 0; j < boxYSize; j++) {
                double y = (boxTop + j) * gridSize + gridSize / 2d;
                double distYLeft = Math.pow(y - edge.left.y, 2);
                double distYRight = Math.pow(y - edge.right.y, 2);
                if (distXLeft + distYLeft < radiusSq || distXRight + distYRight < radiusSq) {
                    map[boxLeft + i][boxTop + j] += 1;
                }
            }
        }
    }

    private void draw(int[][] map) {
        int height = drawPanel.getHeight();
        int width = drawPanel.getWidth();

        for (int i = 0; i < width / gridSize + 1; i++) {
            for (int j = 0; j < height / gridSize + 1; j++) {
                if (map[i][j] > 0) {
                    View.Shapes.Rectangle viewRect = new Rectangle(i * gridSize, j * gridSize, gridSize, gridSize);
                    float heatValue = ((float) map[i][j] / scaling);
                    float hue = 0.6f - (heatValue * 0.6f);
                    viewRect.color = new Color(Color.HSBtoRGB(hue, 1, 0.7f));
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

    public void draw(List<Edge> edgeList) {
        int[][] map = calculateMap(gridSize, edgeList);
        draw(map);
    }

    public void drawCurrentHeatOfNodes(List<Node> nodes) {
        int scaling = Arrays.stream(map).map(row -> Arrays.stream(row).max().orElse(0)).max(Comparator.naturalOrder()).orElse(0);
        for (Node modelNode : nodes) {
            int x = (int) (modelNode.x);
            int y = (int) (modelNode.y);
            float heatValue = ((float) map[x / gridSize][y / gridSize] / scaling);
            float hue = 0.6f - (heatValue * 0.6f);
            View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x, modelNode.y);
            viewNode.color = new Color(Color.HSBtoRGB(hue, 1, 0.7f));
            viewNode.drawBorder = true;
            drawPanel.shapes.add(viewNode);
        }
    }
}
