package Model;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.UnionFind;

import java.util.LinkedList;

/**
 * LifeAlgorithmState holds a list of edges sorted by coverage and a union find data structure to save the connected
 * components of the network.
 */
public class LifeAlgorithmState extends AlgorithmState {

    public LinkedList<Edge> edgesByCoverage;
    public UnionFind unionFind = new UnionFind();

    public LifeAlgorithmPhase currentPhase;
    public LifeAlgorithmPhase nextPhase;

    public Edge currentEdge;

    public LifeAlgorithmState(Graph _origin) {
        super(_origin);
        edgesChosen = new LinkedList<>();
    }

    @Override
    public AlgorithmState clone() {
        LifeAlgorithmState state = new LifeAlgorithmState(origin);
        state.edgesChosen.addAll(this.edgesChosen);
        state.edgesByCoverage = this.edgesByCoverage;
        state.nextPhase = this.nextPhase;
        state.unionFind = this.unionFind;
        state.currentEdge = this.currentEdge;
        return state;
    }
}
