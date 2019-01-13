package Model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Node extends HeapableObject {
    public int id;

    public double x;
    public double y;
    public double radius;

    public List<Edge> edgeList = new LinkedList<>();

    public Node(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Node(double x, double y, double radius,int id) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
    }

    public List<Node> getNeighbours() {
        return edgeList.stream()
                .map(e -> e.getNeighbourOf(this))
                .collect(Collectors.toList());
    }

    public Node cloneWithoutEdges() {
        return new Node(this.x, this.y, this.radius, this.id);
    }

    public double distanceTo(Node other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}
