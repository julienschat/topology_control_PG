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
    public boolean checkIfPointIsCovered(double x,double y){
        double distanceLeft = Math.sqrt(Math.pow(x - left.x, 2) + Math.pow(y - left.y, 2));
        double distanceRight = Math.sqrt(Math.pow(x - right.x, 2) + Math.pow(y - right.y, 2));
        return (distanceLeft <= getLength() || distanceRight <= getLength());
    }
}
