package View;

import DataStructures.Edge;
import DataStructures.Graph;
import Model.*;
import View.Shapes.Circle;

import java.awt.*;

import static Model.LiseAlgorithmPhase.*;

/**
 * AlgorithmsDrawer is the class responsible for visualizing the different algorithms and their current state of operation.
 * It is capable of drawing nodes, edges and coverages in different colors.
 * The drawing differentiates between the different AlgorithmStates, to account for their unique procedure.
 */
public class AlgorithmDrawer {

    private DrawPanel drawPanel;
    private HeatmapDrawer heatMapDrawer;

    public AlgorithmDrawer(DrawPanel panel) {
        this.drawPanel = panel;
        heatMapDrawer = new HeatmapDrawer(panel);
    }

    public void drawNode(DataStructures.Node modelNode, Color color) {
        View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x, modelNode.y);
        viewNode.color = color;
        drawPanel.shapes.add(viewNode);
    }

    public void drawEdge(Edge modelEdge, Color color, int width) {
        View.Shapes.Edge viewEdge = new View.Shapes.Edge(modelEdge.left.x, modelEdge.left.y, modelEdge.right.x, modelEdge.right.y);
        viewEdge.strokeWidth = width;
        viewEdge.color = color;
        drawPanel.shapes.add(viewEdge);
    }

    public void draw(Graph graph, Color color, boolean heatMap, boolean coverage) {
        drawPanel.shapes.clear();

        if (heatMap) {
            heatMapDrawer.draw(graph.edgeList);
        }

        draw(graph, color);

        if (coverage) {
            CoverageDrawer.drawTextLabels(drawPanel, graph);
        }
    }

    private void draw(Graph graph, Color color) {
        for (DataStructures.Node modelNode : graph.nodeList) {
            drawNode(modelNode, color);
        }

        for (Edge edge : graph.edgeList) {
            drawEdge(edge, color, 1);
        }

        drawPanel.update();
    }

    public void drawCoverage(Edge edge, Graph graph, Color color) {
        View.Shapes.Circle circle1 = new Circle(edge.left.x, edge.left.y, edge.getLength());
        View.Shapes.Circle circle2 = new Circle(edge.right.x, edge.right.y, edge.getLength());

        circle1.color = color;
        circle2.color = color;
        drawPanel.shapes.add(circle1);
        drawPanel.shapes.add(circle2);

        drawPanel.update();
    }

    public void drawAlgorithmState(AlgorithmState state, boolean heatMap, boolean coverage) {
        drawPanel.shapes.clear();

        if (heatMap) {
            heatMapDrawer.draw(state.edgesChosen);
        }

        Color colorMax = new Color(255, 153, 153);
        Color colorMin = new Color(204, 229, 255);

        if (state instanceof LiseAlgorithmState) {
            LiseAlgorithmState liseState = (LiseAlgorithmState) state;

            if (liseState.nextPhase != FINISHED) {
                //Pre drawing of origin:

                // Draw the coverages of Min and Max Edge
                if (liseState.currentEdgeMaxCoverage != null)
                    drawCoverage(liseState.currentEdgeMaxCoverage, liseState.origin, colorMax);
                if (liseState.currentPhase == MINEDGECHOOSING || liseState.currentPhase == SAMECOVERAGECHOOSING)
                    drawCoverage(liseState.currentEdgeMinCoverage, liseState.origin, colorMin);

                draw(state.origin, new Color(160, 160, 160));

                //Post drawing of origin:

                //Mark Edges already chosen for new network
                for (Edge modelEdge : state.edgesChosen) {
                    drawEdge(modelEdge, new Color(0, 0, 0), 2);
                }

                // Mark nodes covered by Min and Max Edge
                if (liseState.currentEdgeMaxCoverage != null) {
                    for (DataStructures.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMaxCoverage)) {
                        drawNode(modelNode, Color.red);
                    }
                }
                if (liseState.currentPhase == MINEDGECHOOSING || liseState.currentPhase == SAMECOVERAGECHOOSING) {
                    for (DataStructures.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMinCoverage)) {
                        drawNode(modelNode, Color.blue);
                    }
                }

                // Mark current Max and Min Edge
                if (liseState.currentEdgeMaxCoverage != null) drawEdge(liseState.currentEdgeMaxCoverage, Color.red, 2);
                if (liseState.currentPhase == MINEDGECHOOSING || liseState.currentPhase == SAMECOVERAGECHOOSING)
                    drawEdge(liseState.currentEdgeMinCoverage, Color.blue, 2);

                // Mark the shortest path between v,w of current Max Edge
                if (liseState.shortestPath != null) {
                    for (Edge edge : liseState.shortestPath) {
                        drawEdge(edge, Color.green, 2);
                    }
                }

            } else {
                drawFinishedState(liseState);
            }

        } else if (state instanceof LifeAlgorithmState) {
            LifeAlgorithmState lifeState = (LifeAlgorithmState) state;

            if (lifeState.phase != LifeAlgorithmPhase.FINISHED) {
                //Pre origin
                if (!lifeState.edgesChosen.isEmpty()) {
                    drawCoverage(lifeState.edgesChosen.getFirst(), lifeState.origin, colorMin);
                }

                draw(state.origin, new Color(160, 160, 160));

                //Post origin
                if (!lifeState.edgesChosen.isEmpty()) {
                    for (DataStructures.Node modelNode : lifeState.origin.getCoveredNodesByEdge(lifeState.edgesChosen.getFirst())) {
                        drawNode(modelNode, Color.blue);
                    }
                }

                for (Edge modelEdge : lifeState.edgesChosen) {
                    drawEdge(modelEdge, Color.black, 2);
                }
                if (!lifeState.edgesChosen.isEmpty()) {
                    drawEdge(lifeState.edgesChosen.getFirst(), Color.blue, 2);
                }

            } else {
                drawFinishedState(lifeState);
            }
        } else if (state instanceof LliseAlgorithmState) {
            LliseAlgorithmState lliseState = (LliseAlgorithmState) state;

            if (lliseState.phase == LliseAlgorithmPhase.RUN_PARALLEL) {
                //Pre drawing of origin:

                // Draw the coverage of current edge
                if (lliseState.nodeState.currentEdge != null)
                    drawCoverage(lliseState.nodeState.currentEdge, lliseState.nodeState.origin, colorMax);

                draw(state.origin, new Color(180, 180, 180));
                if (lliseState.nodeState.floodedGraph != null && lliseState.nodeState.nextPhase != LliseNodeAlgorithmPhase.FINISHED) {
                    draw(lliseState.nodeState.floodedGraph, new Color(80, 80, 80));
                    draw(state.origin.cloneGraphWithoutEdges(), new Color(180, 180, 180));
                }

                //Post drawing of origin:

                // Mark current edge
                if (lliseState.nodeState.currentEdge != null) drawEdge(lliseState.nodeState.currentEdge, Color.red, 2);

                if(lliseState.nodeState.currentPhase == LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING){
                    for (Edge modelEdge : lliseState.nodeState.edgesChosen) {
                        drawEdge(modelEdge, new Color(0, 0, 0), 2);
                    }
                }else if (lliseState.nodeState.newTSpannerGraph != null) {
                    //Show current network in which calculations are made
                    for (Edge modelEdge : lliseState.nodeState.newTSpannerGraph.edgeList) {
                        drawEdge(modelEdge, new Color(0, 0, 0), 2);
                    }
                }

                // Mark nodes covered by current Edge
                if (lliseState.nodeState.currentEdge != null) {
                    for (DataStructures.Node modelNode : lliseState.nodeState.origin.getCoveredNodesByEdge(lliseState.nodeState.currentEdge)) {
                        drawNode(modelNode, Color.red);
                    }
                }


                //Mark CurrentNode
                drawNode(lliseState.nodeState.currentNode, Color.black);

                // Mark the shortest path between v,w of current Max Edge
                if (lliseState.nodeState.shortestPath != null) {
                    for (Edge edge : lliseState.nodeState.shortestPath) {
                        drawEdge(edge, Color.green, 2);
                    }
                }

            } else {
                drawFinishedState(lliseState);
            }
        }

        if (coverage) {
            CoverageDrawer.drawTextLabels(drawPanel, state.origin);
        }
        drawPanel.update();
    }

    private void drawFinishedState(AlgorithmState state) {
        draw(state.origin, new Color(160, 160, 160));
        for (Edge modelEdge : state.edgesChosen) {
            drawEdge(modelEdge, Color.black, 2);
        }
    }

    public void updateScaling(java.util.List<Edge> edgeList) {
        this.heatMapDrawer.updateScaling(edgeList);
    }
}
