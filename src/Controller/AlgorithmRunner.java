package Controller;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import Model.LiseAlgorithmState;
import View.AlgorithmDrawer;
import View.GraphDrawer;
import View.Shapes.Edge;
import View.Shapes.Node;
import View.Shapes.Radius;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class AlgorithmRunner implements Runnable {
    private AlgorithmController algorithmController;
    private AlgorithmDrawer drawer;
    private Graph graph;
    private int tSpannerMeasure;

    public AlgorithmRunner(AlgorithmDrawer drawer, Graph graph, AlgorithmController algorithmController) {
        this.drawer = drawer;
        this.graph = graph;
        this.algorithmController = algorithmController;
    }

    public void settSpannerMeasure(int t){
        this.tSpannerMeasure = t;
    }

    @Override
    public void run() {
        try {
            drawer.draw(graph, false,Color.black);
            AlgorithmState state;
            if(algorithmController instanceof LiseAlgorithmController) {
                state = algorithmController.init(graph,tSpannerMeasure);
            }else {
                state = algorithmController.init(graph);
            }

            // running algorithm stepwise

            while (!algorithmController.isFinished(state)) {
                algorithmController.processState(state);

                drawer.drawAlgorithmState(state);
                TimeUnit.MILLISECONDS.sleep(400);
            }

            System.out.println("Finished");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
