package View;

import Model.*;
import View.Shapes.Circle;
import View.Shapes.Node;

import java.awt.*;

import static Model.LiseAlgorithmPhase.*;

public class AlgorithmDrawer {

    private DrawPanel drawPanel;
    private HeatmapDrawer heatMapDrawer;

    public AlgorithmDrawer(DrawPanel panel){
        this.drawPanel = panel;
        heatMapDrawer = new HeatmapDrawer(panel);
    }
    public void drawNode(Model.Node modelNode, boolean drawRadius, Color color){
        View.Shapes.Node viewNode = new View.Shapes.Node(modelNode.x,modelNode.y);
        viewNode.color = color;
        drawPanel.shapes.add(viewNode);
    }

    public void drawEdge(Model.Edge modelEdge, Color color){
        View.Shapes.Edge viewEdge = new View.Shapes.Edge(modelEdge.left.x,modelEdge.left.y,modelEdge.right.x,modelEdge.right.y);
        viewEdge.color = color;
        drawPanel.shapes.add(viewEdge);
    }

    public void draw(Graph graph, boolean radii, Color color){
        for(Model.Node modelNode : graph.nodeList){
            drawNode(modelNode,true,color);
        }

        for(Edge edge: graph.edgeList){
            drawEdge(edge,color);
        }
        drawPanel.update();
    }

    public void drawCoverage(Model.Edge edge,Model.Graph graph,Color color){
        View.Shapes.Circle circle1 = new Circle(edge.left.x,edge.left.y,edge.getLength());
        View.Shapes.Circle circle2 = new Circle(edge.right.x,edge.right.y,edge.getLength());

        circle1.color = color;
        circle2.color = color;
        drawPanel.shapes.add(circle1);
        drawPanel.shapes.add(circle2);

        drawPanel.update();

    }

    public void drawAlgorithmState(AlgorithmState state, boolean heatMap){

        Color colorMax = new Color(255, 153, 153);
        Color colorMin = new Color(204, 229, 255);

        if(state instanceof LiseAlgorithmState){

            drawPanel.shapes.clear();
            LiseAlgorithmState liseState = (LiseAlgorithmState)state;

            //Pre drawing of origin:

            // Draw the coverages of Min and Max Edge
            if (liseState.currentEdgeMaxCoverage!=null) drawCoverage(liseState.currentEdgeMaxCoverage,liseState.origin,colorMax);
            if (liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING ) drawCoverage(liseState.currentEdgeMinCoverage,liseState.origin,colorMin);

            draw(state.origin,false,new Color(120,120,120));

            //Post drawing of origin:

            //Mark Edges already chosen for new network
            for(Model.Edge modelEdge : state.edgesChosen){
                drawEdge(modelEdge,new Color(0,0,0));
            }

            // Mark nodes covered by Min and Max Edge
            if(liseState.currentEdgeMaxCoverage!=null) {
                for (Model.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMaxCoverage)) {
                    drawNode(modelNode, false, Color.red);
                }
            }
            if(liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING ) {
                for (Model.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMinCoverage)) {
                    drawNode(modelNode, false, Color.blue);
                }
            }

            // Mark the shortest path between v,w of current Max Edge
            if(liseState.currentStatesPhase == SHORTESTPATHCHECKING) {
                if (liseState.nodesOnShortestPath != null) {
                    for (int i = 0; i < liseState.nodesOnShortestPath.size() - 1; i++) {
                        Edge newEdge = new Edge(liseState.nodesOnShortestPath.get(i), liseState.nodesOnShortestPath.get(i + 1));
                        drawEdge(newEdge, Color.green);
                    }
                }
            }

            // Mark current Max and Min Edge
            if(liseState.currentEdgeMaxCoverage!=null) drawEdge(liseState.currentEdgeMaxCoverage,Color.red);
            if (liseState.currentStatesPhase == MINEDGECHOOSING || liseState.currentStatesPhase == SAMECOVERAGECHOOSING ) drawEdge(liseState.currentEdgeMinCoverage,Color.blue);



            if(liseState.phase == FINISHED){
                drawFinishedState(liseState, heatMap);
            }

//            switch(liseState.phase){
//                case MAXEDGECHOOSING:
//                    //drawEdge(liseState.currentEdgeMaxCoverage,Color.red);
//                    //drawCoverage(liseState.currentEdgeMaxCoverage,liseState.origin);
//                    break;
//                case SHORTESTPATHCHECKING:
//                    drawEdge(liseState.currentEdgeMaxCoverage,Color.red);
//                    break;
//                case MINEDGECHOOSING:
//                    drawEdge(liseState.currentEdgeMaxCoverage,Color.red);
//                    break;
//                case SAMECOVERAGECHOOSING:
//                    drawEdge(liseState.currentEdgeMaxCoverage,Color.red);
//                    break;
//
//
//            }

        }else if(state instanceof LifeAlgorithmState){
            drawPanel.shapes.clear();
            LifeAlgorithmState lifeState = (LifeAlgorithmState)state;

            //Pre origin
            if(!lifeState.edgesChosen.isEmpty()) {
                drawCoverage(lifeState.edgesChosen.getFirst(), lifeState.origin,colorMin);
            }

            draw(state.origin,false,new Color(120,120,120));

            //Post origin
            if(!lifeState.edgesChosen.isEmpty()) {
                for (Model.Node modelNode : lifeState.origin.getCoveredNodesByEdge(lifeState.edgesChosen.getFirst())) {
                    drawNode(modelNode, false, Color.blue);
                }
            }

            for(Model.Edge modelEdge : lifeState.edgesChosen){
                drawEdge(modelEdge,Color.black);
            }
            if(!lifeState.edgesChosen.isEmpty()) {
                drawEdge(lifeState.edgesChosen.getFirst(), Color.red);
            }

            if(lifeState.phase == LifeAlgorithmPhase.FINISHED){
                drawFinishedState(lifeState, heatMap);
            }
        }
        drawPanel.update();
    }

    public void drawFinishedState(AlgorithmState state, boolean heatMap){
        drawPanel.shapes.clear();
        if (heatMap) {
            this.heatMapDrawer.drawHeatMap(state.edgesChosen);
        }
        draw(state.origin,false, new Color(120,120,120));
        for(Model.Edge modelEdge : state.edgesChosen){
            drawEdge(modelEdge,Color.black);
        }
    }
}
