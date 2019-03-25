package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class LliseNodeController extends AlgorithmController{





    public Graph getFloodedNeighborhood(Edge edge){
        Graph neighborhood = new Graph();
        // Implement flooding
        return neighborhood;
    }

    @Override
    public AlgorithmState init(Graph origin, double... params) {
        LliseNodeAlgorithmState state = new LliseNodeAlgorithmState(null);
        if(params.length > 1){
            state.currentNode = origin.getNodeById((int)params[0]);

        }else{
            state.currentNode = origin.getNodeById(0);
        }

        state.incidentEdges = state.currentNode.edgeList.stream()
                .filter(e -> e.getNeighbourOf(state.currentNode).id < state.currentNode.id)
                .collect(Collectors.toCollection(LinkedList::new));


        return state;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseNodeAlgorithmState state = (LliseNodeAlgorithmState)algorithmState;

        switch(state.phase){
            case CURRENTEDGECHOOSING:
                if(!state.incidentEdges.isEmpty()) {
                    state.currentEdge = state.incidentEdges.getFirst();
                    state.incidentEdges.removeFirst();
                    state.phase = LliseNodeAlgorithmPhase.FLOODING;
                }else{
                    state.phase = LliseNodeAlgorithmPhase.FINISHED;
                }
                break;
            case FLOODING:
                state.origin = getFloodedNeighborhood(state.currentEdge);

                state.origin.calculateCoverages();

                state.edgesSortedByCoverage = state.origin.edgeList.stream()
                        .sorted(Comparator.comparing(e->e.coverage))
                        .collect(Collectors.toCollection(LinkedList::new));

                //Use already chosen Edges in newTSpanner
                state.newTSpannerGraph = state.origin.cloneGraphWithoutEdges();
                state.phase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case SHORTESTPATHCHECKING:
                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentNode.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdge.getNeighbourOf(sourceNode).id);

                Dijkstra dijkstra = new Dijkstra();
                ShortestPathTree shortestPathTree = dijkstra.runDijkstra(state.newTSpannerGraph,sourceNode);

                if(destinationNode.key != -1){
                    state.nodesOnShortestPath = shortestPathTree.getPathToSourceFromNode(destinationNode);
                }else{
                    state.nodesOnShortestPath = null;
                }

                if(destinationNode.key == -1 ||
                        destinationNode.key > state.tSpannerMeasure * state.currentEdge.getLength()){
                    state.phase = LliseNodeAlgorithmPhase.MINEDGECHOOSING;
                }else{
                    state.phase = LliseNodeAlgorithmPhase.CURRENTEDGECHOOSING;
                }
                break;
            case MINEDGECHOOSING:
                if(!state.edgesSortedByCoverage.isEmpty()) {
                    state.edgesChosen.add(state.edgesSortedByCoverage.getFirst());
                    state.edgesSortedByCoverage.removeFirst();
                }
                state.phase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
        }
        return null;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return false;
    }
}
