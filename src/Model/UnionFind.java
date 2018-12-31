package Model;

import java.util.ArrayList;
import java.util.List;

public class UnionFind {
    private ArrayList<InternalNode> universe;

    public class InternalNode {
        InternalNode parent = null;
        int rank = 0;
        Node node;
        InternalNode(Node item) {
            this.node = item;
        }
    }

    public void makeSets(List<Node> nodes) {
        int i = 0;
        universe = new ArrayList<>(nodes.size());
        for (Node node: nodes) {
            node.id = i;
            universe.add(new InternalNode(node));
            i++;
        }
    }

    public Node find(Node node) {
        InternalNode iNode = this.universe.get(node.id);
        while (iNode.parent != null) {
            if (iNode.parent.parent != null) {
                iNode.parent = iNode.parent.parent;
            }
            iNode = iNode.parent;
        }
        return iNode.node;
    }

    public void union(Node node1, Node node2) {
        InternalNode iNode1 = universe.get(find(node1).id);
        InternalNode iNode2 = universe.get(find(node2).id);

        if (iNode1 != iNode2) {
            if (iNode1.rank > iNode2.rank) {
                iNode2.parent = iNode1;
            } else {
                iNode1.parent = iNode2;
                if (iNode1.rank == iNode2.rank) {
                    iNode2.rank++;
                }
            }
        }
    }
}
