package View;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import View.Shapes.Edge;
import View.Shapes.Node;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RunDemo implements Runnable {
    private DrawPanel drawPanel;

    public RunDemo(DrawPanel panel) {
        drawPanel = panel;
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

            TimeUnit.SECONDS.sleep(1);

            AlgorithmController controller = new LifeAlgorithmController();
            AlgorithmState state = controller.init(graph);

            // running algorithm stepwise

            while (!controller.isFinished(state)) {
                controller.processState(state);
                // Begin of Life specific code
                Graph tmp = new Graph(state.origin.nodeList, ((LifeAlgorithmState)state).edgeList);
                drawGraph(tmp);
                // End of Life specific code
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
