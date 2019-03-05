package View;

import Model.AlgorithmState;
import Model.Edge;
import Model.Graph;
import View.Shapes.Node;

import java.awt.*;

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
    public void draw(Graph graph, boolean radii){
        drawPanel.shapes.clear();
        for(Model.Node modelNode : graph.nodeList){
            drawNode(modelNode,true,Color.black);
        }


        for(Edge edge: graph.edgeList){
            drawPanel.shapes.add(new View.Shapes.Edge(edge.left.x,edge.left.y,edge.right.x,edge.right.y));
        }
        drawPanel.update();
    }

    public void drawAlgorithmState(AlgorithmState state){
        
        Graph tmp = new Graph(state.origin.nodeList, state.edgesChosen);
        draw(tmp, false);

    }
}
