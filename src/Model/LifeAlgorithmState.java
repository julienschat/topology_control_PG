package Model;

import java.util.ArrayList;
import java.util.List;

public class LifeAlgorithmState extends AlgorithmState {
    List<Edge> edgeList = new ArrayList<Edge>();
    UnionFind unionFind = new UnionFind();

    public LifeAlgorithmState(Graph _origin) {
        super(_origin);
        unionFind.makeSets(origin.nodeList);
    }
}
