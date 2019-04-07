package Model;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.Node;

import java.util.LinkedList;
import java.util.List;

public class LiseAlternativeState extends LiseAlgorithmState {
    public LinkedList<Edge> edgesByCoverageInCycle = new LinkedList<Edge>();

    public LinkedList<Edge> edgesFinal = new LinkedList<>();

    public LiseAlternativeState(Graph _origin, double tSpannerMeasure){
        super(_origin, tSpannerMeasure);
    }

    public List<Node> getCurrentNodes(){
        return newTSpannerGraph.nodeList;
    }

    @Override
    public AlgorithmState clone() {
        LiseAlternativeState state = new LiseAlternativeState(this.origin, this.tSpannerMeasure);
        super.cloneTo(state);
        state.edgesByCoverageInCycle.addAll(edgesByCoverageInCycle);
        state.edgesFinal.addAll(edgesFinal);
        return state;
    }
}
