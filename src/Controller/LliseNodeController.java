package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
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
                    if(state.newTSpannerGraph.getNodeById(e.left.id)!=null&&state.newTSpannerGraph.getNodeById(e.left.id)!=null){
                        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(e.left.id),state.newTSpannerGraph.getNodeById(e.right.id));
                    }
                }
                state.phase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;

                break;
            case SHORTESTPATHCHECKING:
                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentNode.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdge.getNeighbourOf(sourceNode).id);

                ShortestPathTree shortestPathTree = Dijkstra.runDijkstra(state.newTSpannerGraph,sourceNode);

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
                    state.currentEdgeMinCoverage = state.edgesSortedByCoverage.getFirst();
                    state.edgesChosen.add(state.currentEdgeMinCoverage);
                    addEdgeToTSPanner(algorithmState,state.currentEdgeMinCoverage);
                    state.edgesSortedByCoverage.removeFirst();
                    state.phase = LliseNodeAlgorithmPhase.SAMECOVERAGECHOOSING;
                }else {
                    state.phase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;
                }

                break;
            case SAMECOVERAGECHOOSING:
                if(state.edgesSortedByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage){
                    state.edgesChosen.add(state.edgesSortedByCoverage.getFirst());
                    addEdgeToTSPanner(algorithmState,state.edgesSortedByCoverage.getFirst());

                    state.edgesSortedByCoverage.removeFirst();


                }else{
                    state.phase = LliseNodeAlgorithmPhase.SHORTESTPATHCHECKING;

                }
//                if(state.edgesSortedByCoverage.isEmpty()){
//                    state.phase = LliseNodeAlgorithmPhase.PREFINISHED;
//                }
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
            case SHORTESTPATHCHECKING:
                return "Check if shortest path exists";
            case MINEDGECHOOSING:
                return "Choose Edge with min coverage";
            case SAMECOVERAGECHOOSING:
                return "Choose Edge with same coverage";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
