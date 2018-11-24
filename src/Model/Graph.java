package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Graph {
    public List<Node> nodeList = new ArrayList<>();
    public List<Edge> edgeList = new LinkedList<>();

    public Graph() { }

    public void insertNode(Node n) {
        this.nodeList.add(n);
    }

    public void connectNodes(Node a, Node b) {
        Edge edge = new Edge(a, b);
        a.edgeList.add(edge);
        b.edgeList.add(edge);
        this.edgeList.add(edge);
    }

    public Graph cloneGraphWithoutEdges() {
        Graph newGraph = new Graph();
        newGraph.nodeList = this.nodeList.stream()
                .map(Node::cloneWithoutEdges)
                .collect(Collectors.toList());
        return newGraph;
    }
}
