package Controller;

import DataStructures.Graph;
import Model.AlgorithmState;
import DataStructures.Edge;

abstract public class AlgorithmController {

    public abstract Model.AlgorithmState init(Graph pristine);

    public Model.AlgorithmState next(Model.AlgorithmState state) {
        if (state.next != null) {
            return state.next;
        }
        AlgorithmState nextState = state.clone();
        processState(nextState);
        nextState.step = state.step + 1;
        nextState.previous = state;
        state.next = nextState;
        return nextState;
    }

    public Model.AlgorithmState back(Model.AlgorithmState state) {
        return state.previous == null ? state : state.previous;
    }

    public void calculateFinishedNetwork(Model.AlgorithmState state){
        double[] maxEdgeLengthPerNode = new double[state.origin.nodeList.size()];
        // calculate longest edge for each node
        for(Edge edge : state.edgesChosen){
            double edgeLength = edge.getLength();
            if(maxEdgeLengthPerNode[edge.left.id] < edgeLength){
                maxEdgeLengthPerNode[edge.left.id] = edgeLength;
            }

            if(maxEdgeLengthPerNode[edge.right.id] < edgeLength){
                maxEdgeLengthPerNode[edge.right.id] = edgeLength;
            }
        }
        state.edgesChosen.clear();

        for(Edge edge : state.origin.edgeList){
            double edgeLength = edge.getLength();
            if (maxEdgeLengthPerNode[edge.left.id] >= edgeLength && maxEdgeLengthPerNode[edge.right.id] >= edgeLength) {
                state.edgesChosen.add(edge);
            }
        }
    }

    protected abstract Model.AlgorithmState processState(Model.AlgorithmState algorithmState);

    public abstract boolean isFinished(Model.AlgorithmState algorithmState);

    public abstract String getPhaseDescription(AlgorithmState state);
}
