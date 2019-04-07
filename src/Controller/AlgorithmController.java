package Controller;

import DataStructures.Graph;
import Model.AlgorithmState;
import DataStructures.Edge;

/**
 * The class AlgorithmController is the abstract base class for all the algorithm controllers ( LIFE, LISE, LLISE, LISEALT)
 * which have to implement the two main functions init(Graph pristine) and processState(AlgorithmState state).
 * These functions initialize an algorithm state and calculate the next one from the forwarded one respectively.
 * On top of that they have to implement functions for returning a description of the current step and checking if the algorithm finished so far.
 * AlgorithmController implements functions to move forward and backward in the algorithms history (next, back).
 *
 */
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
