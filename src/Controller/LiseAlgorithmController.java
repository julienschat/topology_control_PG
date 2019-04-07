package Controller;

import DataStructures.*;
import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;


/**
 * LiseAgorithmController is the controller for the LISE algorithm. It implements the two functions init and processState
 * thereby defining the algorithms logic. The algorithm is seperated into five main steps:
 * <p>
 * <ul>
 * <li>In the initializing step the edges are sorted by coverage and the datastructures are instantiated. </li>
 * <li>In the MAXEDGECHOOSING step the edge with maximum coverage is chosen.</li>
 * <li>In the SHORTESTPATHCHECKING step, it is checked wether there exists a path P in the current subgraph with |P| <= t*|u,v|, where u, v are the defining nodes of the maximum edge.</li>
 * <li>In the MINEDGECHOOSING step the edge with minimum coverage is chosen and added to the new subgraph. </li>
 * <li>In the SAMECOVERAGECHOOSING step edges with the same coverage as the minimumedge are also added to the new subgraph.</li>
 * </ul>
 * </p>
 */
public class LiseAlgorithmController extends AlgorithmController {
    private double tMeasure = 2;

    @Override
    public AlgorithmState init(Graph origin) {
        LiseAlgorithmState initState;
        initState = new LiseAlgorithmState(origin, tMeasure);
        origin.fixNodeIDs();
        initState.newTSpannerGraph = origin.cloneGraphWithoutEdges();

        // Create Edgelist for State with sorted (by coverage) Edges of graph
        origin.calculateCoverages();
        initState.edgesByCoverage = origin.edgeList.stream()
                .sorted(Comparator.comparing(e -> e.coverage))
                .collect(Collectors.toCollection(LinkedList::new));


        initState.nextPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;
        initState.currentPhase = LiseAlgorithmPhase.INIT;
        return initState;
    }

    public void setTMeasure(double t) {
        this.tMeasure = t;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {

        LiseAlgorithmState state = (LiseAlgorithmState) algorithmState;

        switch (state.nextPhase) {
            case MAXEDGECHOOSING:
                state.currentPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;

                state.currentEdgeMaxCoverage = state.edgesByCoverage.getLast();
                state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case SHORTESTPATHCHECKING:
                state.currentPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                // Take saved Maximum Edge e={v,w} and check distance between v and w in shortest path tree

                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.left.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.right.id);

                ShortestPathTree shortestPathTree = Dijkstra.runDijkstra(state.newTSpannerGraph, sourceNode);

                //View: Mark path from source to destination
                if (destinationNode.key != -1) {
                    state.shortestPath = shortestPathTree.getPathToSourceFromNode(state.origin, destinationNode);
                } else {
                    // Better would be the ~ best possible path so far
                    state.shortestPath = null;
                }

                if (destinationNode.key == -1 ||
                        destinationNode.key > state.tSpannerMeasure * state.currentEdgeMaxCoverage.getLength()) {

                    state.nextPhase = LiseAlgorithmPhase.MINEDGECHOOSING;
                } else {

                    state.edgesByCoverage.removeLast();
                    if (state.edgesByCoverage.isEmpty()) {
                        state.nextPhase = LiseAlgorithmPhase.PREFINISHED;
                    } else {
                        state.nextPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;

                    }
                }

                break;
            case MINEDGECHOOSING:
                state.currentPhase = LiseAlgorithmPhase.MINEDGECHOOSING;
                // Choose Edge with minimum coverage

                state.currentEdgeMinCoverage = state.edgesByCoverage.getFirst();
                state.edgesByCoverage.removeFirst();
                state.edgesChosen.add(state.currentEdgeMinCoverage);
                addEdgeToTSPanner(algorithmState, state.currentEdgeMinCoverage);


                if (state.edgesByCoverage.isEmpty()) {
                    state.nextPhase = LiseAlgorithmPhase.PREFINISHED;
                } else if (state.edgesByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage) {
                    state.nextPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                } else {
                    state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case SAMECOVERAGECHOOSING:
                state.currentPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                //Add all edges with same coverage as currentEdgeMinCoverage and add them to the graph
                //View: Mark the Edge
                state.edgesChosen.add(state.edgesByCoverage.getFirst());
                addEdgeToTSPanner(algorithmState, state.edgesByCoverage.getFirst());

                state.edgesByCoverage.removeFirst();

                if (state.edgesByCoverage.isEmpty()) {
                    state.nextPhase = LiseAlgorithmPhase.PREFINISHED;
                } else if (state.edgesByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage) {
                    state.nextPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                } else {
                    state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case PREFINISHED:
                calculateFinishedNetwork(state);
                state.nextPhase = LiseAlgorithmPhase.FINISHED;
                break;
        }
        return state;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LiseAlgorithmState) algorithmState).nextPhase == LiseAlgorithmPhase.FINISHED;
    }

    public void addEdgeToTSPanner(AlgorithmState algorithmState, Edge edge) {
        LiseAlgorithmState state = (LiseAlgorithmState) algorithmState;
        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(edge.left.id), state.newTSpannerGraph.getNodeById(edge.right.id));
    }

    @Override
    public String getPhaseDescription(AlgorithmState state) {
        switch (((LiseAlgorithmState) state).currentPhase) {
            case MAXEDGECHOOSING:
                return "Choose Edge with max coverage";
            case SHORTESTPATHCHECKING:
                return "Check if shortest path exists";
            case MINEDGECHOOSING:
                return "Choose Edge with min coverage";
            case SAMECOVERAGECHOOSING:
                return "Choose Edge with same coverage";
            case FINISHED:
                return "Finished";
            case PREFINISHED:
                return "Calculating final network";
            default:
                return "";
        }
    }
}
