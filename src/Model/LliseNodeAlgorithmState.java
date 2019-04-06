package Model;

import java.util.LinkedList;

public class LliseNodeAlgorithmState extends AlgorithmState {
    public LliseNodeAlgorithmPhase phase;

    public LinkedList<Edge> edgesSortedByCoverage = new LinkedList<Edge>();
    public LinkedList<Edge> incidentEdges = new LinkedList<Edge>();
    public Node currentNode;
    public Edge currentEdge,currentEdgeMinCoverage;

    public Graph floodedGraph;
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
        LliseNodeAlgorithmState newState = new LliseNodeAlgorithmState(this.origin);
        newState.tSpannerMeasure = this.tSpannerMeasure;

        newState.currentEdge= this.currentEdge;
        newState.currentEdgeMinCoverage = this.currentEdgeMinCoverage;
        newState.edgesChosen.addAll(this.edgesChosen);
        newState.currentStatesPhase = this.currentStatesPhase;
        newState.phase = this.phase;
        if(this.newTSpannerGraph != null) newState.newTSpannerGraph = this.newTSpannerGraph.cloneGraphWithEdges();
        if(!this.edgesSortedByCoverage.isEmpty()) newState.edgesSortedByCoverage.addAll(this.edgesSortedByCoverage);
        if(this.nodesOnShortestPath!=null) newState.nodesOnShortestPath.addAll(this.nodesOnShortestPath);
        if(this.floodedGraph != null ) newState.floodedGraph = this.floodedGraph.cloneGraphWithEdges();
        if(!incidentEdges.isEmpty()) newState.incidentEdges.addAll(this.incidentEdges);
        newState.currentNode = newState.origin.getNodeById(this.currentNode.id);

        return newState;


    }
}
