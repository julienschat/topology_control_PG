package View;

import Model.Graph;
import View.Shapes.Edge;
import View.Shapes.Radius;
import View.Shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;

public class GraphDrawer extends Observable {
    private DrawPanel drawPanel;

    public Model.Node draggedNode;
    public Model.Node hoveredNode;
    public Model.Node draggedRadiusNode;
    private boolean drawHeatMap = false;

    public GraphDrawer(DrawPanel panel) {
        this.drawPanel = panel;
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

    public void drawHeatMap(Graph graph){
        int height = drawPanel.getHeight();
        int width = drawPanel.getWidth();
        int precision = 10;
        System.out.println("Height: "+height+" width: "+width);
        int numberOfEdges = graph.edgeList.size();

        for(int i = 0; i < width; i+=10){
            for(int j = 0; j < height; j+=10) {
                View.Shapes.Rectangle viewRect = new Rectangle(i, j,precision,precision);
                int count = 0;
                for(Model.Edge modelEdge : graph.edgeList){
                    if(modelEdge.checkIfPointIsCovered(i+precision/2,j+precision/2)) count +=1;
                }
                float heatValue = ((float)count/numberOfEdges);

                if(heatValue>0 && heatValue < 255) System.out.println("HeatValue for x: "+i+" and y: "+j+" is "+heatValue);

                viewRect.color = new Color(heatValue,0f,1-heatValue);
                drawPanel.shapes.add(viewRect);
            }
        }
        drawPanel.update();
    }

    public void draw(Graph graph, boolean radii, boolean heatMap) {
        drawPanel.shapes.clear(); // NOTE: should maybe only delete own shapes.
        if(heatMap) drawHeatMap(graph);
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
