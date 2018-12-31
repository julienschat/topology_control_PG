package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LifeAlgorithmState extends AlgorithmState {
    public LinkedList<Edge> edgeList = new LinkedList<Edge>();
    public LinkedList<Edge> edgesByCoverage;
    public UnionFind unionFind = new UnionFind();

    public LifeAlgorithmPhase phase;

    public LifeAlgorithmState(Graph _origin) {
        super(_origin);
    }
}
