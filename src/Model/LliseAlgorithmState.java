package Model;

import DataStructures.Edge;
import DataStructures.Graph;

import java.util.LinkedList;
import java.util.List;

/**
 * The LliseAlgorithmState maintains always one LliseNodeAlgorithmState which in theory would be multiple in parallel.
 */
public class LliseAlgorithmState extends AlgorithmState {

    public LliseNodeAlgorithmState nodeState;
    public LliseAlgorithmPhase phase;

    public int currentNodeID;
    public LinkedList<List<Edge>> edgesChosenByNodes = new LinkedList<>();

    public LliseAlgorithmState(Graph _origin) {
        super(_origin);
        edgesChosen = new LinkedList<>();
    }

    @Override
    public AlgorithmState clone() {
        LliseAlgorithmState newState = new LliseAlgorithmState(origin);
        newState.nodeState = (LliseNodeAlgorithmState) nodeState.clone();
        newState.phase = phase;
        newState.currentNodeID = currentNodeID;
        newState.edgesChosenByNodes.addAll(edgesChosenByNodes);
        newState.edgesChosen.addAll(edgesChosen);
        return newState;
    }
}
