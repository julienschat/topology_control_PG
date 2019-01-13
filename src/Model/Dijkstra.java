package Model;

import java.util.*;

public class Dijkstra {

    public ShortestPathTree runDijkstra(Graph graph, Node source){
        ShortestPathTree shortestPathTree = new ShortestPathTree(graph, source.id);
        MinHeap distanceHeap = new MinHeap(graph.nodeList.size());

        //Could be implemented with Array
        LinkedList<Node> reachedNodes = new LinkedList<Node>();



        source.key = 0;
        for(Node n : graph.nodeList){
            n.key = -1;
        }
        distanceHeap.insert(source);

        while(!distanceHeap.isEmpty()){
            Node currentNode = ((Node)distanceHeap.extractMin());
            reachedNodes.add(currentNode);

            for(Node neighbour : currentNode.getNeighbours()){

                if(!reachedNodes.contains(neighbour)){

                    if(neighbour.key == -1){

                        neighbour.key = currentNode.key + currentNode.distanceTo(neighbour);
                        distanceHeap.insert(neighbour);

                    }else{

                        if(currentNode.key + currentNode.distanceTo(neighbour) < neighbour.key) {
                            distanceHeap.decreaseKey(neighbour, currentNode.key + currentNode.distanceTo(neighbour));
                            shortestPathTree.previousNode[neighbour.id] = currentNode;

                        }
                    }
                }
            }
        }

        return shortestPathTree;
    }
}
