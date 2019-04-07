package Controller;

import DataStructures.*;
import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * The class LliseNodeController implements the logic of the LLISE algorithm in a single node.
 */
public class LliseNodeController extends AlgorithmController {
    private Node currentNode;
    private double tMeasure;

    @Override
    public AlgorithmState init(Graph origin) {
        LliseNodeAlgorithmState state = new LliseNodeAlgorithmState(origin, tMeasure);
        state.currentNode = currentNode;

        state.incidentEdges = state.currentNode.edgeList.stream()
                .filter(e -> e.getNeighbourOf(state.currentNode).id < state.currentNode.id)
                .collect(Collectors.toCollection(LinkedList::new));

        state.nextPhase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
        state.currentPhase = LliseNodeAlgorithmPhase.INIT;
        return state;
    }

    public void setTMeasure(double t) {
        this.tMeasure = t;
    }

    public void setNode(Node node) {
        currentNode = node;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState) algorithmState;
        state.currentPhase = state.nextPhase;
        switch (state.nextPhase) {
            case CURRENTEDGECHOOSING:
                state.floodedGraph = null;
                if (!state.incidentEdges.isEmpty()) {
                    state.currentEdge = state.incidentEdges.pop();
                    state.nextPhase = LliseNodeAlgorithmPhase.FLOODING;
                } else {
                    state.nextPhase = LliseNodeAlgorithmPhase.FINISHED;
                }
                break;
            case FLOODING:
                state.floodedGraph = getFloodedNeighborhood(state.origin, state.currentEdge, state.tSpannerMeasure);

                state.floodedGraph.calculateCoverages();

                state.edgesByCoverage = state.floodedGraph.edgeList.stream()
                        .filter(e -> !state.edgesChosen.contains(state.origin.getEdgeByIds(e.left.id, e.right.id)))
                        .sorted(Comparator.comparing(e -> e.coverage))
                        .collect(Collectors.toCollection(LinkedList::new));

                //Use already chosen Edges in newTSpanner
                state.newTSpannerGraph = state.floodedGraph.cloneGraphWithoutEdges();
                for (Edge e : state.edgesChosen) {
                    if (state.newTSpannerGraph.getNodeById(e.left.id) != null && state.newTSpannerGraph.getNodeById(e.left.id) != null) {
                        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(e.left.id), state.newTSpannerGraph.getNodeById(e.right.id));
                    }
                }
                state.nextPhase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case MINCOVERAGEADDING:
                if (!state.edgesByCoverage.isEmpty()) {
                    state.currentEdgeMinCoverage = state.edgesByCoverage.pop();
                    addEdgeToTSPanner(algorithmState, state.currentEdgeMinCoverage);

                    while (!state.edgesByCoverage.isEmpty() &&
                            state.edgesByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage) {
                        Edge sameCoverage = state.edgesByCoverage.pop();
                        addEdgeToTSPanner(algorithmState, sameCoverage);
                    }
                    state.nextPhase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;
                } else {
                    state.nextPhase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
                }
                break;
            case SHORTESTPATHCHECKING:

                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentNode.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdge.getNeighbourOf(state.currentNode).id);

                ShortestPathTree shortestPathTree = Dijkstra.runDijkstra(state.newTSpannerGraph, sourceNode);

                if (destinationNode.key == -1 ||
                        destinationNode.key > state.tSpannerMeasure * state.currentEdge.getLength()) {
                    state.nextPhase = LliseNodeAlgorithmPhase.MINCOVERAGEADDING;
                } else {
                    state.shortestPath = shortestPathTree.getPathToSourceFromNode(state.origin, destinationNode);
                    state.edgesChosen.addAll(state.shortestPath);
                    state.nextPhase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;


                }


                break;
        }
        return state;
    }

    public Graph getFloodedNeighborhood(Graph graph, Edge edge, double t) {
        return Dijkstra.getKNeighbourhood(graph, edge, edge.getLength() * t / 2);
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LliseNodeAlgorithmState) algorithmState).nextPhase == LliseNodeAlgorithmPhase.FINISHED;
    }

    public void addEdgeToTSPanner(AlgorithmState algorithmState, Edge edge) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState) algorithmState;
        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(edge.left.id), state.newTSpannerGraph.getNodeById(edge.right.id));
    }

    @Override
    public String getPhaseDescription(AlgorithmState _state) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState) _state;
        switch (state.currentPhase) {
            case FLOODING:
                return "Calculate flooded neighbourhood";
            case SHORTESTPATHCHECKING:
                return "Checking for shortest path";
            case CURRENTEDGECHOOSING:
                return "Choose current Edge";
            case MINCOVERAGEADDING:
                return "Adding all edges with min coverage";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
