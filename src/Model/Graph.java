package Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
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

    public List<Node> getNodesInRange(Node node, double range) {
        // this could be improved by using a spatial tree
        return this.nodeList.stream()
                .filter(o -> node.distanceTo(o) <= range)
                .filter(o -> o != node)
                .collect(Collectors.toList());

    }

    public Graph cloneGraphWithoutEdges() {
        Graph newGraph = new Graph();
        newGraph.nodeList = this.nodeList.stream()
                .map(Node::cloneWithoutEdges)
                .collect(Collectors.toList());
        return newGraph;
    }

    public Graph reducedGraph(List<Edge> edges) {
        Graph newGraph = cloneGraphWithoutEdges();
        for (Edge edge: edges) {
            int lIndex = edge.left.index;
            int rIndex = edge.right.index;
            newGraph.connectNodes(newGraph.nodeList.get(lIndex), newGraph.nodeList.get(rIndex));
        }
        return newGraph;
    }

    public static Graph readFile(String fileName) throws IOException {
        Graph graph = new Graph();
        Scanner sc = new Scanner(Paths.get(fileName));
        while (sc.hasNext()) {
            graph.nodeList.add(new Node(sc.nextDouble(), sc.nextDouble(), sc.nextDouble()));
        }
        sc.close();
        for (Node node : graph.nodeList) {
            for (Node other: graph.getNodesInRange(node, node.radius)) {
                if (!node.getNeighbours().contains(other)) {
                    graph.connectNodes(node, other);
                }
            }
        }
        return graph;
    }

    public void fixNodeIndicies() {
        int index = 0;
        for (Node node: nodeList) {
            node.index = index;
            index++;
        }
    }

    public void calculateCoverages() {
        for (Node node: nodeList) {
            node.edgeList.sort(Comparator.comparingDouble(e -> e.left.distanceTo(e.right)));
            for (int i = 1; i < node.edgeList.size(); i++) {
                node.edgeList.get(i).coverage += i;
            }
        }
    }
}
