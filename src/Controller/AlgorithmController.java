package Controller;

import Model.AlgorithmState;
import Model.Edge;
import Model.Node;

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

    public void calculateFinishedNetwork(Model.AlgorithmState state){
        Edge[] maxEdgePerNode = new Edge[state.origin.nodeList.size()];
        for(Edge edge : state.edgesChosen){
            if(maxEdgePerNode[edge.left.id] !=null){
                if(edge.getLength()>maxEdgePerNode[edge.left.id].getLength()) {
                    maxEdgePerNode[edge.left.id] = edge;
                }
            }else{
                maxEdgePerNode[edge.left.id] = edge;
            }

            if(maxEdgePerNode[edge.right.id] !=null){
                if(edge.getLength()>maxEdgePerNode[edge.right.id].getLength()) {
                    maxEdgePerNode[edge.right.id] = edge;
                }
            }else{
                maxEdgePerNode[edge.right.id] = edge;
            }
        }
        state.edgesChosen.clear();
        for(Edge edge : state.origin.edgeList){
            if((maxEdgePerNode[edge.left.id]!=null&&maxEdgePerNode[edge.left.id].getLength()>=edge.getLength())&&(maxEdgePerNode[edge.right.id]!=null&&maxEdgePerNode[edge.right.id].getLength()>=edge.getLength())){
                state.edgesChosen.add(edge);
            }
        }
    }

    protected abstract Model.AlgorithmState processState(Model.AlgorithmState algorithmState);

    public abstract boolean isFinished(Model.AlgorithmState algorithmState);
}
