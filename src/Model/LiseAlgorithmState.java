package Model;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.Node;

import java.util.LinkedList;
import java.util.List;

public class LiseAlgorithmState extends AlgorithmState {

    public LinkedList<Edge> edgesByCoverage = new LinkedList<Edge>();

    public Edge currentEdgeMinCoverage, currentEdgeMaxCoverage;
    public Graph newTSpannerGraph;
    public double tSpannerMeasure;
    public LiseAlgorithmPhase phase;
    public LiseAlgorithmPhase currentStatesPhase;
    public LinkedList<Edge> shortestPath;

    public LiseAlgorithmState(Graph _origin, double tSpannerMeasure){
        super(_origin);
        this.tSpannerMeasure = tSpannerMeasure;
        edgesChosen = new LinkedList<Edge>();
        shortestPath = new LinkedList<Edge>();
    }

    public void cloneTo(LiseAlgorithmState target) {
        target.currentEdgeMaxCoverage = this.currentEdgeMaxCoverage;
        target.currentEdgeMinCoverage = this.currentEdgeMinCoverage;
        target.edgesChosen.addAll(this.edgesChosen);
        target.currentStatesPhase = this.currentStatesPhase;
        target.phase = this.phase;
        if (newTSpannerGraph != null) {
            target.newTSpannerGraph = this.newTSpannerGraph.cloneGraphWithEdges();
        }
        target.edgesByCoverage.addAll(this.edgesByCoverage);
    }

    @Override
    public AlgorithmState clone() {
        LiseAlgorithmState newState = new LiseAlgorithmState(this.origin,this.tSpannerMeasure);
        cloneTo(newState);
        return newState;
    }
}
