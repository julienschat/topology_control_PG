package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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

        initState.newTSpannerGraph = origin.cloneGraphWithoutEdges();

        // Create Edgelist for State with sorted (by coverage) Edges of graph
        origin.calculateCoverages();
        initState.edgesSortedByCoverage = origin.edgeList.stream()
                .sorted(Comparator.comparing(e -> e.coverage))
                .collect(Collectors.toCollection(LinkedList::new));


        initState.phase = LiseAlgorithmPhase.MAXEDGECHOOSING;
        return initState;
    }

    @Override
    public void processState(AlgorithmState algorithmState) {

        switch(((LiseAlgorithmState)algorithmState).phase) {
            case MAXEDGECHOOSING:
                // Choose Maximum Edge from SortedEdges and remove it
                ((LiseAlgorithmState) algorithmState).currentEdgeMaxCoverage = ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.getLast();
                ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.SHORTESTPATHCHECKING;
            break;
            case SHORTESTPATHCHECKING:
                // Take saved Maximum Edge e={v,w} and check distance between v and w in shortest path tree

                Node sourceNode = ((LiseAlgorithmState) algorithmState).newTSpannerGraph.getNodeById(((LiseAlgorithmState) algorithmState).currentEdgeMaxCoverage.left.id);
                Node destinationNode = ((LiseAlgorithmState) algorithmState).newTSpannerGraph.getNodeById(((LiseAlgorithmState) algorithmState).currentEdgeMaxCoverage.right.id);


                Dijkstra dijkstraAlgorithm = new Dijkstra();
                ShortestPathTree shortestPathTree = dijkstraAlgorithm.runDijkstra(((LiseAlgorithmState) algorithmState).newTSpannerGraph, sourceNode);

                //View: Mark path from source to destination
                if(destinationNode.key != -1) {
                   // LinkedList<Node> nodesOnShortestPath = shortestPathTree.getPathToSourceFromNode(destinationNode);
                }
                if(destinationNode.key == -1 ||
                        destinationNode.key > ((LiseAlgorithmState) algorithmState).tSpannerMeasure * sourceNode.distanceTo(destinationNode)){

                    ((LiseAlgorithmState) algorithmState).phase = LiseAlgorithmPhase.MINEDGECHOOSING;
                }else{

                    ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.removeLast();
                    if(((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.isEmpty()){
                        ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.FINISHED;
                    }else {
                        ((LiseAlgorithmState) algorithmState).phase = LiseAlgorithmPhase.MAXEDGECHOOSING;
                    }
                }

            break;
            case MINEDGECHOOSING:
                // Choose Edge with minimum coverage

                ((LiseAlgorithmState) algorithmState).currentEdgeMinCoverage = ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.getFirst();
                ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.removeFirst();
                ((LiseAlgorithmState) algorithmState).edgesChosen.add(((LiseAlgorithmState) algorithmState).currentEdgeMinCoverage);
                addEdgeToTSPanner(algorithmState,((LiseAlgorithmState) algorithmState).currentEdgeMinCoverage);
                // View: Mark the minCoverage Edge
                ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.SAMECOVERAGECHOOSING;
                if(((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.isEmpty()){
                    ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.FINISHED;
                }

                break;

            case SAMECOVERAGECHOOSING:

                //Add all edges with same coverage as currentEdgeMinCoverage and add them to the graph
                if(((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.getFirst().coverage == ((LiseAlgorithmState) algorithmState).currentEdgeMinCoverage.coverage){
                    //View: Mark the Edge
                    ((LiseAlgorithmState) algorithmState).edgesChosen.add(((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.getFirst());
                    addEdgeToTSPanner(algorithmState,((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.getFirst());

                    ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.removeFirst();


                }else{
                    ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.MAXEDGECHOOSING;
                    ((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.removeLast();
                }
                if(((LiseAlgorithmState) algorithmState).edgesSortedByCoverage.isEmpty()){
                    ((LiseAlgorithmState)algorithmState).phase = LiseAlgorithmPhase.FINISHED;
                }

                break;
        }
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LiseAlgorithmState)algorithmState).phase == LiseAlgorithmPhase.FINISHED;
    }

    public void addEdgeToTSPanner(AlgorithmState algorithmState, Edge edge){
        System.out.println("Edge is added to TPSpanner, from: "+edge.left.id+" to "+edge.right.id);
        ((LiseAlgorithmState) algorithmState).newTSpannerGraph.connectNodes(((LiseAlgorithmState) algorithmState).newTSpannerGraph.getNodeById(edge.left.id),((LiseAlgorithmState) algorithmState).newTSpannerGraph.getNodeById(edge.right.id));
    }


}
