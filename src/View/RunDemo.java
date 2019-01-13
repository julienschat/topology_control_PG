package View;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import Model.LiseAlgorithmState;
import View.Shapes.Edge;
import View.Shapes.Node;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RunDemo implements Runnable {
    private DrawPanel drawPanel;
    private AlgorithmController algorithmController;

    public RunDemo(DrawPanel panel, AlgorithmController algorithmController) {
        drawPanel = panel;
        this.algorithmController = algorithmController;
    }

    // following method belongs into a dedicated controller
    public void drawGraph(Graph graph) {
        drawPanel.shapes.clear();
        for (Model.Node node : graph.nodeList) {
            drawPanel.shapes.add(new Node(node.x, node.y));
        }
        for (Model.Edge edge: graph.edgeList) {
            drawPanel.shapes.add(new Edge(edge.left.x, edge.left.y, edge.right.x, edge.right.y));
        }

        drawPanel.update();
    }

    @Override
    public void run() {
        try {
            // reading a graph

            Graph graph = Graph.readFile("./test_graph.txt");

            drawGraph(graph);

            TimeUnit.SECONDS.sleep(5);


            AlgorithmState state = algorithmController.init(graph);

            // running algorithm stepwise

            while (!algorithmController.isFinished(state)) {
                algorithmController.processState(state);
                // Begin of Life specific code
                Graph tmp = new Graph(state.origin.nodeList, state.edgesChosen);
                drawGraph(tmp);
                // End of Life specific code
                TimeUnit.MILLISECONDS.sleep(400);
            }
            System.out.println("Finished");
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
