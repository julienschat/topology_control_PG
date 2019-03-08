package View;

import Model.*;
import View.Shapes.Circle;
import View.Shapes.Node;

import java.awt.*;

import static Model.LiseAlgorithmPhase.*;

public class AlgorithmDrawer {

    private DrawPanel drawPanel;

    public AlgorithmDrawer(DrawPanel panel){
        this.drawPanel = panel;
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

    public void drawCoverage(Model.Edge edge,Model.Graph graph){
        View.Shapes.Circle circle1 = new Circle(edge.left.x,edge.left.y,edge.getLength());
        View.Shapes.Circle circle2 = new Circle(edge.right.x,edge.right.y,edge.getLength());
        Color color = new Color(255,247,247);
        circle1.color = color;
        circle2.color = color;
        drawPanel.shapes.add(circle1);
        drawPanel.shapes.add(circle2);

        drawPanel.update();

    }

    public void drawAlgorithmState(AlgorithmState state){

        if(state instanceof LiseAlgorithmState){

            drawPanel.shapes.clear();
            LiseAlgorithmState liseState = (LiseAlgorithmState)state;

            //Pre origin
            drawCoverage(liseState.currentEdgeMaxCoverage,liseState.origin);

            draw(state.origin,false,new Color(120,120,120));

            //Post origin

            for(Model.Edge modelEdge : state.edgesChosen){
                drawEdge(modelEdge,new Color(0,0,0));
            }

            for(Model.Node modelNode : liseState.origin.getCoveredNodesByEdge(liseState.currentEdgeMaxCoverage)){
                drawNode(modelNode,false,Color.red);
            }

            drawEdge(liseState.currentEdgeMaxCoverage,Color.red);

            drawPanel.update();

            if(liseState.phase == FINISHED){
                drawFinishedState(liseState);
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
                drawCoverage(lifeState.edgesChosen.getFirst(), lifeState.origin);
            }

            draw(state.origin,false,new Color(120,120,120));

            //Post origin
            if(!lifeState.edgesChosen.isEmpty()) {
                for (Model.Node modelNode : lifeState.origin.getCoveredNodesByEdge(lifeState.edgesChosen.getFirst())) {
                    drawNode(modelNode, false, Color.red);
                }
            }

            for(Model.Edge modelEdge : lifeState.edgesChosen){
                drawEdge(modelEdge,Color.black);
            }
            if(!lifeState.edgesChosen.isEmpty()) {
                drawEdge(lifeState.edgesChosen.getFirst(), Color.red);
            }

            if(lifeState.phase == LifeAlgorithmPhase.FINISHED){
                drawFinishedState(lifeState);
            }

            drawPanel.update();
        }




    }

    public void drawFinishedState(AlgorithmState state){
        drawPanel.shapes.clear();
        draw(state.origin,false,new Color(120,120,120));
        for(Model.Edge modelEdge : state.edgesChosen){
            drawEdge(modelEdge,Color.black);
        }
    }
}
