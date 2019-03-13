package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LiseAlgorithmState extends AlgorithmState {

    public LinkedList<Edge> edgesSortedByCoverage = new LinkedList<Edge>();

    public Edge currentEdgeMinCoverage,currentEdgeMaxCoverage;
    public Graph newTSpannerGraph;
    public double tSpannerMeasure;
    public LiseAlgorithmPhase phase;
    public LiseAlgorithmPhase currentStatesPhase;
    public LinkedList<Node> nodesOnShortestPath;

    public LiseAlgorithmState(Graph _origin, double tSpannerMeasure){
        super(_origin);
        this.tSpannerMeasure = tSpannerMeasure;
        edgesChosen = new LinkedList<Edge>();
    }

    public List<Node> getCurrentNodes(){
        return newTSpannerGraph.nodeList;
    }

    @Override
    public AlgorithmState clone() {
        LiseAlgorithmState newState = new LiseAlgorithmState(this.origin,this.tSpannerMeasure);
        newState.edgesChosen.addAll(this.edgesChosen);
        newState.currentStatesPhase = this.currentStatesPhase;
        newState.phase = this.phase;
        newState.newTSpannerGraph = this.newTSpannerGraph.cloneGraphWithEdges();
        newState.nodesOnShortestPath.addAll(this.nodesOnShortestPath);
        return newState;

    }
}
