package DataStructures;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The graph class offers functionalities to create, update and retrieve imformation from a graph datastructure.
 */
public class Graph {
    public List<Node> nodeList = new ArrayList<>();
    public List<Edge> edgeList = new LinkedList<>();

    public Graph() {
    }

    public Graph(List<Node> nodes, List<Edge> edges) {
        nodeList = nodes;
        edgeList = edges;
    }

    public void insertNode(Node node) {
        nodeList.add(node);

        updateNeighbours(node);
    }

    public void updateNeighbours(Node node) {
        for (Edge edge : node.edgeList) {
            edge.getNeighbourOf(node).edgeList.remove(edge);
        }

        this.edgeList.removeAll(node.edgeList);
        node.edgeList.clear();

        for (Node other : getNodesInRange(node, node.radius).collect(Collectors.toList())) {
            if (other.isInRange(node)) {
                connectNodes(node, other);
            }
        }
    }

    public List<Node> getCoveredNodesByEdge(Edge edge) {
        double length = edge.getLength();
        List<Node> coveredNodes = this.getNodesInRange(edge.left, length).collect(Collectors.toList());
        coveredNodes.addAll(this.getNodesInRange(edge.right, length).collect(Collectors.toList()));
        return coveredNodes;
    }

    public boolean areNodesConnected(Node a, Node b) {
        return a.getNeighbours().contains(b);
    }

    public void connectNodes(Node a, Node b) {
        Edge edge = new Edge(a, b);
        a.edgeList.add(edge);
        b.edgeList.add(edge);
        this.edgeList.add(edge);
    }

    public Node getNodeById(int idOfNode) {
        return nodeList.stream().filter(n -> idOfNode == n.id).findFirst().orElse(null);
    }

    public Stream<Node> getNodesInRange(Node node, double range) {
        // this could be improved by using a spatial tree
        return this.nodeList.stream()
                .filter(o -> node.distanceTo(o) <= range)
                .filter(o -> o != node);
    }

    public Graph cloneGraphWithoutEdges() {
        Graph newGraph = new Graph();
        newGraph.nodeList = this.nodeList.stream()
                .map(Node::cloneWithoutEdges)
                .collect(Collectors.toList());
        return newGraph;
    }

    public Graph cloneGraphWithEdges() {
        fixNodeIDs();
        Graph newGraph = cloneGraphWithoutEdges();
        for (Edge e : edgeList) {

            newGraph.connectNodes(newGraph.getNodeById(e.left.id), newGraph.getNodeById(e.right.id));

        }
        return newGraph;
    }

    public Graph reducedGraph(List<Edge> edges) {
        Graph newGraph = cloneGraphWithoutEdges();
        for (Edge edge : edges) {
            int lIndex = edge.left.index;
            int rIndex = edge.right.index;
            newGraph.connectNodes(newGraph.nodeList.get(lIndex), newGraph.nodeList.get(rIndex));
        }
        return newGraph;
    }

    public static Graph readFile(String fileName) throws IOException {
        Graph graph = new Graph();

        int id = 0;
        for (String line : Files.readAllLines(Paths.get(fileName))) {
            String[] values = line.split(" ");
            if (values.length > 0) {
                if (values.length != 3) {
                    throw new IOException("File has wrong format");
                }
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                double r = Double.parseDouble(values[2]);
                graph.insertNode(new Node(x, y, r, id));
                id++;
            }
        }

        return graph;
    }

    public static void writeFile(Graph graph, String fileName) throws IOException {
        List<String> lines = graph.nodeList.stream()
                .map(n -> String.join(" ", Double.toString(n.x), Double.toString(n.y), Double.toString(n.radius)))
                .collect(Collectors.toList());

        Files.write(Paths.get(fileName), lines, Charset.defaultCharset());
    }

    public void fixNodeIDs() {
        int id = 0;
        for (Node node : nodeList) {
            node.id = id;
            id++;
        }
    }

    public void calculateCoverages() {
        for (Edge edge : edgeList) {
            double length = edge.getLength();
            Set<Node> nodes = this.getNodesInRange(edge.left, length).collect(Collectors.toSet());
            nodes.addAll(this.getNodesInRange(edge.right, length).collect(Collectors.toSet()));
            edge.coverage = nodes.size();
        }
    }

    public void printGraph() {
        System.out.println("Printing graph:");
        System.out.println("#Knoten: " + nodeList.size());
        System.out.println("#Kanten: " + edgeList.size());
        for (Node node : nodeList) {
            System.out.print("Knoten ID: " + node.id + ", ");
            System.out.print("Kanten zu: ");

            for (Edge edge : node.edgeList) {
                System.out.print(edge.getNeighbourOf(node).id + ", ");
            }
            System.out.println("");
        }
    }

    public void removeNode(Node node) {
        for (Edge edge : node.edgeList) {
            edge.getNeighbourOf(node).edgeList.remove(edge);
            edgeList.remove(edge);
        }
        nodeList.remove(node);
    }

    public Edge getEdgeByIds(int leftId, int rightId) {
        Node left = this.getNodeById(leftId);
        Node right = this.getNodeById(rightId);

        return left.edgeList.stream()
                .filter(e -> e.getNeighbourOf(left) == right)
                .findFirst().orElse(null);
    }
}
