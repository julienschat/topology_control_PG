package Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    public List<Node> nodeList = new ArrayList<>();
    public List<Edge> edgeList = new LinkedList<>();

    public Graph() { }

    public Graph(List<Node> nodes, List<Edge> edges) {
        nodeList = nodes;
        edgeList = edges;
    }

    public void insertNode(Node n) {
        nodeList.add(n);

        for (Node other: getNodesInRange(n, n.radius)) {
            connectNodes(n, other);
        }
    }


    public void connectNodes(Node a, Node b) {
        Edge edge = new Edge(a, b);
        a.edgeList.add(edge);
        b.edgeList.add(edge);
        this.edgeList.add(edge);
    }
    public Node getNodeById(int idOfNode){
        return nodeList.stream().filter(n->idOfNode==n.id).findFirst().orElse(null);
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
        int id = 0;
        while (sc.hasNext()) {
            graph.insertNode(new Node(sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), id));
            id++;
        }
        sc.close();
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
