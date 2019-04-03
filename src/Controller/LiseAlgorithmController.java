package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;



public class LiseAlgorithmController extends AlgorithmController{
    @Override
    public AlgorithmState init(Graph origin, double... params){
        LiseAlgorithmState initState;
        if(params.length>0) {
            initState = new LiseAlgorithmState(origin, params[0]);
        }else{
            initState = new LiseAlgorithmState(origin, 40);
        }
        origin.fixNodeIDs();
        initState.newTSpannerGraph = origin.cloneGraphWithoutEdges();

        // Create Edgelist for State with sorted (by coverage) Edges of graph
        origin.calculateCoverages();
        initState.edgesSortedByCoverage = origin.edgeList.stream()
                .sorted(Comparator.comparing(e -> e.coverage))
                .collect(Collectors.toCollection(LinkedList::new));


        initState.phase = LiseAlgorithmPhase.MAXEDGECHOOSING;
        initState.currentStatesPhase = LiseAlgorithmPhase.INIT;
        return initState;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {

        LiseAlgorithmState state = (LiseAlgorithmState) algorithmState;

        switch(state.phase) {
            case MAXEDGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.MAXEDGECHOOSING;

                state.currentEdgeMaxCoverage = state.edgesSortedByCoverage.getLast();
                state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;

            break;
            case SHORTESTPATHCHECKING:
                state.currentStatesPhase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
                // Take saved Maximum Edge e={v,w} and check distance between v and w in shortest path tree

                Node sourceNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.left.id);
                Node destinationNode = state.newTSpannerGraph.getNodeById(state.currentEdgeMaxCoverage.right.id);


                Dijkstra dijkstraAlgorithm = new Dijkstra();
                ShortestPathTree shortestPathTree = dijkstraAlgorithm.runDijkstra(state.newTSpannerGraph, sourceNode);

                //View: Mark path from source to destination
                if(destinationNode.key != -1) {
                   state.nodesOnShortestPath = shortestPathTree.getPathToSourceFromNode(destinationNode);
                   System.out.println("Found path");
                }else{
                    // Better would be the ~ best possible path so far
                    state.nodesOnShortestPath = null;
                }

                if(destinationNode.key == -1 ||
                        destinationNode.key > state.tSpannerMeasure * sourceNode.distanceTo(destinationNode)){

                    state.phase = LiseAlgorithmPhase.MINEDGECHOOSING;
                }else{

                    state.edgesSortedByCoverage.removeLast();
                    if(state.edgesSortedByCoverage.isEmpty()){
                        state.phase = LiseAlgorithmPhase.PREFINISHED;
                    }else {
                        state.phase = LiseAlgorithmPhase.MAXEDGECHOOSING;

                    }
                }

            break;
            case MINEDGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.MINEDGECHOOSING;
                // Choose Edge with minimum coverage

                state.currentEdgeMinCoverage = state.edgesSortedByCoverage.getFirst();
                state.edgesSortedByCoverage.removeFirst();
                state.edgesChosen.add(state.currentEdgeMinCoverage);
                addEdgeToTSPanner(algorithmState,state.currentEdgeMinCoverage);

                state.phase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                if(state.edgesSortedByCoverage.isEmpty()){
                    state.phase = LiseAlgorithmPhase.PREFINISHED;
                }

                break;

            case SAMECOVERAGECHOOSING:
                state.currentStatesPhase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                //Add all edges with same coverage as currentEdgeMinCoverage and add them to the graph
                if(state.edgesSortedByCoverage.getFirst().coverage == state.currentEdgeMinCoverage.coverage){
                    //View: Mark the Edge
                    state.edgesChosen.add(state.edgesSortedByCoverage.getFirst());
                    addEdgeToTSPanner(algorithmState,state.edgesSortedByCoverage.getFirst());

                    state.edgesSortedByCoverage.removeFirst();


                }else{
                    state.phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;

                }
                if(state.edgesSortedByCoverage.isEmpty()){
                    state.phase = LiseAlgorithmPhase.PREFINISHED;
                }

                break;

            case PREFINISHED:
                calculateFinishedNetwork(state);
                state.phase = LiseAlgorithmPhase.FINISHED;
                break;
        }
        return state;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LiseAlgorithmState)algorithmState).phase == LiseAlgorithmPhase.FINISHED;
    }

    public void addEdgeToTSPanner(AlgorithmState algorithmState, Edge edge){
        LiseAlgorithmState state = (LiseAlgorithmState) algorithmState;
        state.newTSpannerGraph.connectNodes(state.newTSpannerGraph.getNodeById(edge.left.id),state.newTSpannerGraph.getNodeById(edge.right.id));
    }
}
