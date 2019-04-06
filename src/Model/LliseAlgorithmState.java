package Model;

import java.util.LinkedList;

public class LliseAlgorithmState extends AlgorithmState{

    public LliseNodeAlgorithmState nodeState;
    public LliseAlgorithmPhase phase;

    public LliseAlgorithmState(Graph _origin) {
        super(_origin);


    }
    @Override
    public AlgorithmState clone() {
        LliseAlgorithmState newState = new LliseAlgorithmState(origin);
        newState.nodeState = (LliseNodeAlgorithmState)nodeState.clone();
        return newState;
    }
}
