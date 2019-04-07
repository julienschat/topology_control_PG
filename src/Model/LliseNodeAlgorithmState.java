package Model;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.Node;

import java.util.LinkedList;

/**
 * The LliseNodeAlgorithmState is the LLISE state for one particular node.
 * Additionally to the normal LiseStatus it remembers the node it works on, the flooded neighbourhood and holds
 * a list of incidentEdges.
 */
public class LliseNodeAlgorithmState extends LiseAlgorithmState {
    public LliseNodeAlgorithmPhase phase;

    public LinkedList<Edge> incidentEdges = new LinkedList<>();
    public Node currentNode;
    public Edge currentEdge;

    public Graph floodedGraph;

    public LliseNodeAlgorithmState(Graph _origin, double tSpan) {
        super(_origin, tSpan);
    }

    @Override
    public AlgorithmState clone() {
        LliseNodeAlgorithmState newState = new LliseNodeAlgorithmState(this.origin, tSpannerMeasure);
        super.cloneTo(newState);

        newState.phase = this.phase;
        newState.currentEdge = this.currentEdge;

        if (this.floodedGraph != null) {
            newState.floodedGraph = this.floodedGraph.cloneGraphWithEdges();
        }
        if (!incidentEdges.isEmpty()) {
            newState.incidentEdges.addAll(this.incidentEdges);
        }
        newState.currentNode = this.currentNode;

        return newState;
    }
}
