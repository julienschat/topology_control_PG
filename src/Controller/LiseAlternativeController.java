package Controller;

import DataStructures.*;
import Model.*;

import java.util.LinkedList;


public class LiseAlternativeController extends LiseAlgorithmController{
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

        switch(state.phase) {
            case MAXEDGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;

                state.edgesByCoverageInCycle = new LinkedList<>();
                state.edgesByCoverageInCycle.addAll(state.edgesByCoverage);

                state.currentEdgeMaxCoverage = state.edgesByCoverage.getLast();
                state.edgesByCoverage.removeLast();

                state.edgesChosen = new LinkedList<>();
                state.edgesChosen.addAll(state.edgesFinal);

                state.newTSpannerGraph = state.origin.cloneGraphWithoutEdges();
                for (Edge edge: state.edgesChosen) {
                    state.newTSpannerGraph.connectNodes(edge.left, edge.right);
                }

                state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case SHORTESTPATHCHECKING:
                state.currentStatesPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                // Take saved Maximum Edge e={v,w} and check distance between v and w in shortest path tree

                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.left.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.right.id);

                ShortestPathTree shortestPathTree = Dijkstra.runDijkstra(state.newTSpannerGraph, sourceNode);

                //View: Mark path from source to destination
                if(destinationNode.key != -1) {
                    state.shortestPath = shortestPathTree.getPathToSourceFromNode(state.origin, destinationNode);
                }else{
                    // Better would be the ~ best possible path so far
                    state.shortestPath = null;
                }

                if(destinationNode.key == -1 ||
                        destinationNode.key > state.tSpannerMeasure * state.currentEdgeMaxCoverage.getLength()){

                    state.phase = LiseAlgorithmPhase.MINEDGECHOOSING;
                }else{
                    // found path

                    state.edgesFinal.addAll(state.shortestPath);
                    state.edgesByCoverage.removeAll(state.shortestPath);

                    if(state.edgesByCoverage.isEmpty()){
                        state.phase = LiseAlgorithmPhase.PREFINISHED;
                    }else {
                        state.phase = LiseAlgorithmPhase.MAXEDGECHOOSING;
                    }
                }

                break;
            case MINEDGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.MINEDGECHOOSING;
                // Choose Edge with minimum coverage

                state.currentEdgeMinCoverage = state.edgesByCoverageInCycle.pop();
                state.edgesChosen.add(state.currentEdgeMinCoverage);
                addEdgeToTSPanner(algorithmState, state.currentEdgeMinCoverage);

                if(!state.edgesByCoverageInCycle.isEmpty()){
                    state.phase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                } else {
                    state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case SAMECOVERAGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                //Add all edges with same coverage as currentEdgeMinCoverage and add them to the graph
                if(state.edgesByCoverageInCycle.getFirst().coverage == state.currentEdgeMinCoverage.coverage){
                    //View: Mark the Edge
                    state.edgesChosen.add(state.edgesByCoverageInCycle.getFirst());
                    addEdgeToTSPanner(algorithmState, state.edgesByCoverageInCycle.getFirst());
                    state.edgesByCoverageInCycle.removeFirst();
                }else{
                    state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }
                if(state.edgesByCoverageInCycle.isEmpty()){
                    state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;

            case PREFINISHED:
                state.edgesChosen = state.edgesFinal;
                calculateFinishedNetwork(state);
                state.phase = LiseAlgorithmPhase.FINISHED;
                break;
        }
        return state;
    }
}
