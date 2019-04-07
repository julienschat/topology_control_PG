package Controller;

import DataStructures.*;
import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class LliseNodeController extends AlgorithmController{
    private Node currentNode;

    @Override
    public AlgorithmState init(Graph origin) {
        LliseNodeAlgorithmState state = new LliseNodeAlgorithmState(origin);
        state.currentNode = currentNode;

        state.incidentEdges = state.currentNode.edgeList.stream()
                .filter(e -> e.getNeighbourOf(state.currentNode).id < state.currentNode.id)
                .collect(Collectors.toCollection(LinkedList::new));

        state.phase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
        return state;
    }

    public void setNode(Node node) {
        currentNode = node;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState)algorithmState;

        switch(state.phase){
            case CURRENTEDGECHOOSING:
                if(!state.incidentEdges.isEmpty()) {
                    state.currentEdge = state.incidentEdges.pop();
                    state.phase = LliseNodeAlgorithmPhase.FLOODING;
                }else{
                    state.phase = LliseNodeAlgorithmPhase.FINISHED;
                }
                break;
            case FLOODING:
                state.floodedGraph = getFloodedNeighborhood(state.origin, state.currentEdge, state.tSpannerMeasure);

                state.floodedGraph.calculateCoverages();

                state.edgesSortedByCoverage = state.floodedGraph.edgeList.stream()
                        .sorted(Comparator.comparing(e->e.coverage))
                        .collect(Collectors.toCollection(LinkedList::new));

                //Use already chosen Edges in newTSpanner
                state.newTSpannerGraph = state.floodedGraph.cloneGraphWithoutEdges();
                for(Edge e : state.edgesChosen){
                    if(state.newTSpannerGraph.getNodeById(e.left.id) != null && state.newTSpannerGraph.getNodeById(e.left.id) != null){
                        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(e.left.id),state.newTSpannerGraph.getNodeById(e.right.id));
                    }
                }
                state.phase = LliseNodeAlgorithmPhase.MINCOVERAGEADDING;

                break;
            case MINCOVERAGEADDING:
                if(!state.edgesSortedByCoverage.isEmpty()) {
                    state.currentEdgeMinCoverage = state.edgesSortedByCoverage.pop();
                    addEdgeToTSPanner(algorithmState, state.currentEdgeMinCoverage);

                    while (!state.edgesSortedByCoverage.isEmpty() &&
                        state.edgesSortedByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage) {
                        Edge sameCoverage = state.edgesSortedByCoverage.pop();
                        addEdgeToTSPanner(algorithmState, sameCoverage);
                    }

                    Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentNode.id);
                    Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdge.getNeighbourOf(state.currentNode).id);

                    ShortestPathTree shortestPathTree = Dijkstra.runDijkstra(state.newTSpannerGraph,sourceNode);

                    if(destinationNode.key == -1 ||
                            destinationNode.key > state.tSpannerMeasure * state.currentEdge.getLength()){
                        state.phase = LliseNodeAlgorithmPhase.MINCOVERAGEADDING;
                    }else{
                        state.shortestPath = shortestPathTree.getPathToSourceFromNode(state.origin, destinationNode);
                        state.edgesChosen.addAll(state.shortestPath);
                        state.phase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
                    }

                } else {
                    state.phase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
                }
                break;
        }
        return state;
    }

    public Graph getFloodedNeighborhood(Graph graph, Edge edge, double t){
        return Dijkstra.getKNeighbourhood(graph, edge, edge.getLength() * t / 2);
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LliseNodeAlgorithmState)algorithmState).phase == LliseNodeAlgorithmPhase.FINISHED;
    }

    public void addEdgeToTSPanner(AlgorithmState algorithmState, Edge edge){
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState)algorithmState;
        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(edge.left.id),state.newTSpannerGraph.getNodeById(edge.right.id));
    }

    @Override
    public String getPhaseDescription(AlgorithmState _state) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState) _state;
        switch (state.phase) {
            case FLOODING:
                return "Calculate flooded neighbourhood";
            case CURRENTEDGECHOOSING:
                return "Choose current Edge";
            case MINCOVERAGEADDING:
                return "Adding all edges with min coverage and check if shortest path exists";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
