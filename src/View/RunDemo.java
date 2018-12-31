package View;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import View.Shapes.Edge;
import View.Shapes.Node;
import View.Shapes.Radius;

import java.io.IOException;
import java.util.Comparator;
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
            double dist = node.edgeList.stream()
                    .map(e -> e.left.distanceTo(e.right))
                    .max(Comparator.naturalOrder())
                    .orElse((double) 0);
            if (dist > 0) {
                drawPanel.shapes.add(new Radius(node.x, node.y, dist));
            }
            for (Model.Node other : graph.nodeList) {
                if (node.distanceTo(other) < dist) {
                    if (!node.getNeighbours().contains(other)) {
                        Edge shapeEdge = new Edge(node.x, node.y, other.x, other.y);
                        shapeEdge.highlight = true;
                        drawPanel.shapes.add(shapeEdge);
                    }
                }
            }
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

            TimeUnit.SECONDS.sleep(2);

            AlgorithmController controller = new LifeAlgorithmController();
            AlgorithmState state = controller.init(graph);

            // running algorithm stepwise

            while (!controller.isFinished(state)) {
                controller.processState(state);
                // Begin of Life specific code
                LifeAlgorithmState lState = (LifeAlgorithmState)state;
                Graph tmp = graph.reducedGraph(lState.edgeList);
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
