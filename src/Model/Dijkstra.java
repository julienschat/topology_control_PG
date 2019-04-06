package Model;

import java.util.*;

public class Dijkstra {
    public static ShortestPathTree runDijkstra(Graph graph, Node source){
        ShortestPathTree shortestPathTree = new ShortestPathTree(graph, source.id);
        MinHeap distanceHeap = new MinHeap(graph.nodeList.size());

        //Could be implemented with Array
        LinkedList<Node> reachedNodes = new LinkedList<Node>();

        for(Node n : graph.nodeList){
            n.key = -1;
        }
        source.key = 0;
        distanceHeap.insert(source);

        while(!distanceHeap.isEmpty()){
            Node currentNode = ((Node)distanceHeap.extractMin());
            reachedNodes.add(currentNode);

            for(Node neighbour : currentNode.getNeighbours()){

                if(!reachedNodes.contains(neighbour)){

                    if(neighbour.key == -1){

                        neighbour.key = currentNode.key + currentNode.distanceTo(neighbour);
                        shortestPathTree.previousNode[neighbour.id] = currentNode;
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

    public static Graph getKNeighbourhood(Graph graph, Edge edge, double k) {
        graph.fixNodeIDs();
        Graph neighbourhood = graph.cloneGraphWithoutEdges();
        neighbourhood.connectNodes(neighbourhood.getNodeById(edge.left.id), neighbourhood.getNodeById(edge.right.id));

        MinHeap distanceHeap = new MinHeap(graph.nodeList.size());

        for (Node n : graph.nodeList) {
            n.key = -1;
        }
        edge.left.key = 0;
        edge.right.key = 0;
        distanceHeap.insert(edge.left);
        distanceHeap.insert(edge.right);

        while (!distanceHeap.isEmpty()) {
            Node currentNode = ((Node)distanceHeap.extractMin());

            for (Edge adjacentEdge: currentNode.edgeList) {
                double edgeLength = adjacentEdge.getLength();
                Node leftInNeighbourhood = neighbourhood.getNodeById(adjacentEdge.left.id);
                Node rightInNeighbourhood = neighbourhood.getNodeById(adjacentEdge.right.id);
                if (!neighbourhood.areNodesConnected(leftInNeighbourhood, rightInNeighbourhood) && currentNode.key + edgeLength < k) {
                    neighbourhood.connectNodes(leftInNeighbourhood, rightInNeighbourhood);
                    Node neighbourInOriginal = adjacentEdge.getNeighbourOf(currentNode);

                    if (neighbourInOriginal.key == -1) {
                        neighbourInOriginal.key = currentNode.key + edgeLength;
                        distanceHeap.insert(neighbourInOriginal);
                    } else {
                        if (currentNode.key + currentNode.distanceTo(neighbourInOriginal) < neighbourInOriginal.key) {
                            distanceHeap.decreaseKey(neighbourInOriginal, currentNode.key + currentNode.distanceTo(neighbourInOriginal));
                        }
                    }
                }

            }
        }

        return neighbourhood;
    }
}
