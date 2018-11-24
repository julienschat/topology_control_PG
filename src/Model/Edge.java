package Model;

public class Edge {
    private Node left;
    private Node right;

    public Edge(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public Node getNeighbourOf(Node n) {
        if (n == this.left) {
            return this.right;
        } else {
            return this.left;
        }
    }
}
