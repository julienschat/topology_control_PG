package Model;

import DataStructures.Edge;
import DataStructures.Graph;

import java.util.LinkedList;

/**
 *
 */
public class LiseAlternativeState extends LiseAlgorithmState {
    public LinkedList<Edge> edgesByCoverageInCycle = new LinkedList<Edge>();

    public LinkedList<Edge> edgesFinal = new LinkedList<>();

    public LiseAlternativeState(Graph _origin, double tSpannerMeasure) {
        super(_origin, tSpannerMeasure);
    }

    @Override
    public AlgorithmState clone() {
        LiseAlternativeState state = new LiseAlternativeState(this.origin, this.tSpannerMeasure);
        super.cloneTo(state);
        state.edgesByCoverageInCycle.addAll(edgesByCoverageInCycle);
        state.edgesFinal.addAll(edgesFinal);
        state.shortestPath = shortestPath;
        return state;
    }
}
