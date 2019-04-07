package Controller;

import DataStructures.*;
import Model.*;

import java.util.LinkedList;


/**
 * The class LiseAlternativeController implements the logic of a variant of the LISE algorithm, which only transfers edges on the
 * found path between the nodes of the maximum edge to the resulting subgraph
 */
public class LiseAlternativeController extends LiseAlgorithmController {
    @Override
    public AlgorithmState init(Graph origin) {
        LiseAlgorithmState liseState = (LiseAlgorithmState) super.init(origin);
        LiseAlternativeState state = new LiseAlternativeState(liseState.origin, liseState.tSpannerMeasure);
        liseState.cloneTo(state);
        return state;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {

        LiseAlternativeState state = (LiseAlternativeState) algorithmState;
        state.currentPhase = state.nextPhase;

        switch (state.nextPhase) {
            case MAXEDGECHOOSING:
                state.edgesByCoverageInCycle = new LinkedList<>();
                state.edgesByCoverageInCycle.addAll(state.edgesByCoverage);

                state.currentEdgeMaxCoverage = state.edgesByCoverage.getLast();
                state.edgesByCoverage.removeLast();

                state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case SHORTESTPATHCHECKING:
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
                    // found path
                    state.nextPhase = LiseAlgorithmPhase.PATHADDING;
                }
                break;
            case PATHADDING:
                state.edgesFinal.addAll(state.shortestPath);
                state.edgesByCoverage.removeAll(state.shortestPath);

                state.edgesChosen = new LinkedList<>();
                state.edgesChosen.addAll(state.edgesFinal);

                state.newTSpannerGraph = state.origin.cloneGraphWithoutEdges();
                for (Edge edge : state.edgesChosen) {
                    state.newTSpannerGraph.connectNodes(edge.left, edge.right);
                }

                state.shortestPath = null;

                if (state.edgesByCoverage.isEmpty()) {
                    state.nextPhase = LiseAlgorithmPhase.PREFINISHED;
                } else {
                    state.nextPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;
                }
                break;
            case MINEDGECHOOSING:
                // Choose Edge with minimum coverage

                state.shortestPath = null;

                state.currentEdgeMinCoverage = state.edgesByCoverageInCycle.pop();
                state.edgesChosen.add(state.currentEdgeMinCoverage);
                addEdgeToTSPanner(algorithmState, state.currentEdgeMinCoverage);

                if (!state.edgesByCoverageInCycle.isEmpty()) {
                    state.nextPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                } else {
                    state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case SAMECOVERAGECHOOSING:
                //Add all edges with same coverage as currentEdgeMinCoverage and add them to the graph
                if (state.edgesByCoverageInCycle.getFirst().coverage == state.currentEdgeMinCoverage.coverage) {
                    //View: Mark the Edge
                    state.edgesChosen.add(state.edgesByCoverageInCycle.getFirst());
                    addEdgeToTSPanner(algorithmState, state.edgesByCoverageInCycle.getFirst());
                    state.edgesByCoverageInCycle.removeFirst();
                } else {
                    state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }
                if (state.edgesByCoverageInCycle.isEmpty()) {
                    state.nextPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case PREFINISHED:
                state.edgesChosen = state.edgesFinal;
                calculateFinishedNetwork(state);
                state.nextPhase = LiseAlgorithmPhase.FINISHED;
                break;
        }
        return state;
    }

    @Override
    public String getPhaseDescription(AlgorithmState state) {
        if (((LiseAlgorithmState)state).currentPhase == LiseAlgorithmPhase.PATHADDING) {
            return "Adding path to final graph";
        } else {
            return super.getPhaseDescription(state);
        }
    }
}
