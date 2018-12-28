package Model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    public double x;
    public double y;
    public double radius;

    public List<Edge> edgeList = new LinkedList<>();

    public Node(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public List<Node> getNeighbours() {
        return edgeList.stream()
                .map(e -> e.getNeighbourOf(this))
                .collect(Collectors.toList());
    }

    public Node cloneWithoutEdges() {
        return new Node(this.x, this.y, this.radius);
    }

    public double distanceTo(Node other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}
