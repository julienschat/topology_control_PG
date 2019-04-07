package Model;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.UnionFind;

import java.util.LinkedList;

public class LifeAlgorithmState extends AlgorithmState {

    public LinkedList<Edge> edgesByCoverage;
    public UnionFind unionFind = new UnionFind();

    public LifeAlgorithmPhase phase;

    public LifeAlgorithmState(Graph _origin) {
        super(_origin);
        edgesChosen = new LinkedList<>();
    }

    @Override
    public AlgorithmState clone() {
        LifeAlgorithmState state = new LifeAlgorithmState(origin);
        state.edgesChosen.addAll(this.edgesChosen);
        state.edgesByCoverage = this.edgesByCoverage;
        state.phase = this.phase;
        state.unionFind = this.unionFind;
        return state;
    }
}
