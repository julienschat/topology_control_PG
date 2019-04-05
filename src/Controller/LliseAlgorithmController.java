package Controller;

import Model.*;

import java.util.LinkedList;

public class LliseAlgorithmController extends AlgorithmController {
    private LinkedList<LliseNodeAlgorithmState> states = new LinkedList<>();
    private LliseNodeController nodeController = new LliseNodeController();
    @Override
    public AlgorithmState init(Graph origin, double... params) {
        double tMeasure;
        if(params.length>0) {
            tMeasure = params[0];
        }else{
            tMeasure = 1;
        }
        origin.fixNodeIDs();
        for(Node node : origin.nodeList){

            LliseNodeAlgorithmState state = (LliseNodeAlgorithmState)nodeController.init(origin,node.id);
            state.tSpannerMeasure = tMeasure;
            states.add(state);

        }
        return states.getFirst();
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseNodeAlgorithmState currentState = (LliseNodeAlgorithmState)algorithmState;
        if(nodeController.isFinished(currentState)){
            states.removeFirst();
            currentState = states.getFirst();
        }else {
            nodeController.processState(currentState);
        }

        return currentState;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return states.isEmpty();
    }
}
