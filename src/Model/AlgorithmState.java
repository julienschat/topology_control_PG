package Model;

import DataStructures.Edge;
import DataStructures.Graph;

import java.util.LinkedList;


public abstract class AlgorithmState {
    public Graph origin;
    public LinkedList<Edge> edgesChosen;
    public AlgorithmState previous = null;
    public AlgorithmState next = null;
    public int step = 0;

    public AlgorithmState(Graph _origin) {
        origin = _origin;
    }

    public abstract AlgorithmState clone();
}
