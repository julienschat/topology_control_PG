package Model;

public class Edge {
    public Node left;
    public Node right;
    public int coverage = 0;

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


    public double getLength() {
        return this.left.distanceTo(this.right);
    }

    public void remove() {
        left.edgeList.remove(this);
        right.edgeList.remove(this);
    }
}
