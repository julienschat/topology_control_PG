package Controller;

import Model.AlgorithmState;

abstract public class AlgorithmController {

    public abstract Model.AlgorithmState init(Model.Graph pristine, double... params);

    public Model.AlgorithmState next(Model.AlgorithmState state) {
        if (state.next != null) {
            return state.next;
        }
        AlgorithmState nextState = state.clone();
        processState(nextState);
        nextState.previous = state;
        state.next = nextState;
        return nextState;
    }

    public Model.AlgorithmState back(Model.AlgorithmState state) {
        return state.previous == null ? state : state.previous;
    }

    protected abstract Model.AlgorithmState processState(Model.AlgorithmState algorithmState);

    public abstract boolean isFinished(Model.AlgorithmState algorithmState);
}
