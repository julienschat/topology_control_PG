package Model;

import java.util.LinkedList;

public class LliseNodeAlgorithmState extends AlgorithmState {
    public LliseNodeAlgorithmPhase phase;

    public LinkedList<Edge> edgesSortedByCoverage = new LinkedList<Edge>();
    public LinkedList<Edge> incidentEdges = new LinkedList<Edge>();
    public Node currentNode;
    public Edge currentEdge,currentEdgeMinCoverage;

    public Graph newTSpannerGraph;
    public double tSpannerMeasure;

    public LliseNodeAlgorithmPhase currentStatesPhase;
    public LinkedList<Node> nodesOnShortestPath;

    public LliseNodeAlgorithmState(Graph _origin) {
        super(_origin);
        edgesChosen = new LinkedList<Edge>();
        nodesOnShortestPath = new LinkedList<Node>();
    }

    @Override
    public AlgorithmState clone() {
        return null;
    }
}
