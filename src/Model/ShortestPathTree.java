package Model;

import java.util.LinkedList;
import java.util.List;

public class ShortestPathTree {

    public int numberOfNodes;
    //public Graph shortestPathGraph;
    public Node[] previousNode;
    public int sourceId;


    public ShortestPathTree(Graph originGraph, int sourceId){

        previousNode = new Node[originGraph.nodeList.size()];
        //shortestPathGraph = originGraph.cloneGraphWithoutEdges();
        numberOfNodes = originGraph.nodeList.size();
        this.sourceId = sourceId;

    }

    public LinkedList<Node> getPathToSourceFromNode(Node destinationNode){
        LinkedList<Node> path = new LinkedList<Node>();
        Node currentNode = destinationNode;
        path.add(currentNode);
        while(currentNode.id != sourceId){
            path.add(previousNode[currentNode.id]);
            currentNode = previousNode[currentNode.id];
        }
        return path;
    }

    //public HashMap<Node,Integer> distance = new HashMap<Node,Integer>();




}
