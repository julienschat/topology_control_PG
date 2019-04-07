package DataStructures;

import java.util.LinkedList;

/**
 * Implementation of a Shortest-path tree offering functionality to find the shortestpath between the root and some node.
 */
public class ShortestPathTree {

    public int numberOfNodes;
    //public Graph shortestPathGraph;
    public Node[] previousNode;
    public int sourceId;

    public ShortestPathTree(Graph originGraph, int sourceId) {

        previousNode = new Node[originGraph.nodeList.size()];
        //shortestPathGraph = originGraph.cloneGraphWithoutEdges();
        numberOfNodes = originGraph.nodeList.size();
        this.sourceId = sourceId;

    }

    public LinkedList<Edge> getPathToSourceFromNode(Graph origin, Node destinationNode) {
        LinkedList<Edge> path = new LinkedList<>();
        Node currentNode = destinationNode;
        while (currentNode.id != sourceId) {
            path.add(origin.getEdgeByIds(currentNode.id, previousNode[currentNode.id].id));
            currentNode = previousNode[currentNode.id];
        }
        return path;
    }

    //public HashMap<Node,Integer> distance = new HashMap<Node,Integer>();
}
