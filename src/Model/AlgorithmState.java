package Model;

import java.util.LinkedList;


public abstract class AlgorithmState {
    public Graph origin;
    public LinkedList<Edge> edgesChosen;
    public AlgorithmState previous = null;
    public AlgorithmState next = null;

    public AlgorithmState(Graph _origin) {
        origin = _origin;
    }

    public abstract AlgorithmState clone();
}
