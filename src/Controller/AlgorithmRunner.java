package Controller;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import Model.LiseAlgorithmState;
import View.GraphDrawer;
import View.Shapes.Edge;
import View.Shapes.Node;
import View.Shapes.Radius;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class AlgorithmRunner implements Runnable {
    private AlgorithmController algorithmController;
    private GraphDrawer drawer;
    private Graph graph;

    public AlgorithmRunner(GraphDrawer drawer, Graph graph, AlgorithmController algorithmController) {
        this.drawer = drawer;
        this.graph = graph;
        this.algorithmController = algorithmController;
    }

    @Override
    public void run() {
        try {
            drawer.draw(graph, false);

            AlgorithmState state = algorithmController.init(graph);

            // running algorithm stepwise

            while (!algorithmController.isFinished(state)) {
                algorithmController.processState(state);

                Graph tmp = new Graph(state.origin.nodeList, state.edgesChosen);
                drawer.draw(tmp, false);

                TimeUnit.MILLISECONDS.sleep(400);
            }
            System.out.println("Finished");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
