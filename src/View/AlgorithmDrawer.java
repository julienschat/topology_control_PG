package View;

import Model.*;
import View.Shapes.Circle;
import View.Shapes.Node;
import View.Shapes.Text;

import java.awt.*;

import static Model.LiseAlgorithmPhase.*;

public class AlgorithmDrawer {

    private DrawPanel drawPanel;
    private HeatmapDrawer heatMapDrawer;

    public AlgorithmDrawer(DrawPanel panel){
        this.drawPanel = panel;
        heatMapDrawer = new HeatmapDrawer(panel);
    }
    public void drawNode(Model.Node modelNode, Color color){
        View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x,modelNode.y);
        viewNode.color = color;
        drawPanel.shapes.add(viewNode);
    }

    public void drawEdge(Model.Edge modelEdge, Color color, int width){
        View.Shapes.Edge viewEdge = new View.Shapes.Edge(modelEdge.left.x,modelEdge.left.y,modelEdge.right.x,modelEdge.right.y);
        viewEdge.strokeWidth = width;
        viewEdge.color = color;
        drawPanel.shapes.add(viewEdge);
    }

    public void draw(Graph graph, Color color, boolean heatMap, boolean coverage){
        drawPanel.shapes.clear();

        if (heatMap) {
            heatMapDrawer.draw(graph.edgeList);
        }

        draw(graph, color);

        if (coverage) {
            CoverageDrawer.drawTextLabels(drawPanel, graph);
        }
    }

    private void draw(Graph graph, Color color){
        for(Model.Node modelNode : graph.nodeList){
            drawNode(modelNode,color);
        }

        for(Edge edge: graph.edgeList){
            drawEdge(edge, color, 1);
        }

        drawPanel.update();
    }

    public void drawCoverage(Model.Edge edge, Model.Graph graph, Color color){
        View.Shapes.Circle circle1 = new Circle(edge.left.x,edge.left.y,edge.getLength());
        View.Shapes.Circle circle2 = new Circle(edge.right.x,edge.right.y,edge.getLength());

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

            if (liseState.phase != FINISHED) {
                //Pre drawing of origin:

                // Draw the coverages of Min and Max Edge
                if (liseState.currentEdgeMaxCoverage != null)
                    drawCoverage(liseState.currentEdgeMaxCoverage, liseState.origin, colorMax);
                if (liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING)
                    drawCoverage(liseState.currentEdgeMinCoverage, liseState.origin, colorMin);

                draw(state.origin, new Color(160, 160, 160));

                //Post drawing of origin:

                //Mark Edges already chosen for new network
                for (Model.Edge modelEdge : state.edgesChosen) {
                    drawEdge(modelEdge, new Color(0, 0, 0), 2);
                }

                // Mark nodes covered by Min and Max Edge
                if (liseState.currentEdgeMaxCoverage != null) {
                    for (Model.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMaxCoverage)) {
                        drawNode(modelNode, Color.red);
                    }
                }
                if (liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING) {
                    for (Model.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMinCoverage)) {
                        drawNode(modelNode, Color.blue);
                    }
                }

                // Mark the shortest path between v,w of current Max Edge
                if (liseState.currentStatesPhase == SHORTESTPATHCHECKING) {
                    if (liseState.nodesOnShortestPath != null) {
                        for (int i = 0; i < liseState.nodesOnShortestPath.size() - 1; i++) {
                            Edge newEdge = new Edge(liseState.nodesOnShortestPath.get(i), liseState.nodesOnShortestPath.get(i + 1));
                            drawEdge(newEdge, Color.green, 2);
                        }
                    }
                }

                // Mark current Max and Min Edge
                if (liseState.currentEdgeMaxCoverage != null) drawEdge(liseState.currentEdgeMaxCoverage, Color.red, 2);
                if (liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING)
                    drawEdge(liseState.currentEdgeMinCoverage, Color.blue, 2);

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
                    for (Model.Node modelNode : lifeState.origin.getCoveredNodesByEdge(lifeState.edgesChosen.getFirst())) {
                        drawNode(modelNode, Color.blue);
                    }
                }

                for (Model.Edge modelEdge : lifeState.edgesChosen) {
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

                if (lliseState.nodeState.floodedGraph != null && lliseState.nodeState.phase != LliseNodeAlgorithmPhase.FINISHED) {
                    draw(state.origin, new Color(220, 220, 220));
                    draw(lliseState.nodeState.floodedGraph, new Color(80, 80, 80));
                } else {
                    draw(state.origin, new Color(160, 160, 160));
                }

                //Post drawing of origin:

                if (lliseState.nodeState.newTSpannerGraph != null) {
                    //Show current network in which calculations are made
                    for (Model.Edge modelEdge : lliseState.nodeState.newTSpannerGraph.edgeList) {
                        drawEdge(modelEdge, new Color(0, 0, 0), 2);
                    }
                }

                // Mark nodes covered by current Edge
                if (lliseState.nodeState.currentEdge != null) {
                    for (Model.Node modelNode : lliseState.nodeState.origin.getCoveredNodesByEdge(lliseState.nodeState.currentEdge)) {
                        drawNode(modelNode, Color.red);
                    }
                }

                // Mark current edge
                if (lliseState.nodeState.currentEdge != null) drawEdge(lliseState.nodeState.currentEdge, Color.red, 2);

                //Mark CurrentNode
                drawNode(lliseState.nodeState.currentNode, Color.black);

                // Mark the shortest path between v,w of current Max Edge
                if (lliseState.nodeState.nodesOnShortestPath != null) {
                    for (int i = 0; i < lliseState.nodeState.nodesOnShortestPath.size() - 1; i++) {
                        Edge newEdge = new Edge(lliseState.nodeState.nodesOnShortestPath.get(i), lliseState.nodeState.nodesOnShortestPath.get(i + 1));
                        drawEdge(newEdge, Color.green, 2);
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

    private void drawFinishedState(AlgorithmState state){
        draw(state.origin, new Color(160, 160, 160));
        for(Model.Edge modelEdge : state.edgesChosen){
            drawEdge(modelEdge, Color.black, 2);
        }
    }

    public void updateScaling(java.util.List<Model.Edge> edgeList) {
        this.heatMapDrawer.updateScaling(edgeList);
    }
}
