package Controller;

import Model.*;

import java.util.LinkedList;

import static Model.LliseAlgorithmPhase.FINISHED;
import static Model.LliseAlgorithmPhase.RUNNING;

public class LliseAlgorithmController extends AlgorithmController{
    private LliseNodeController nodeController;

    private int currentNodeID;
    @Override
    public AlgorithmState init(Graph origin, double... params) {

        double tMeasure;
        if(params.length>0) {
            tMeasure = params[0];
        }else{
            tMeasure = 1;
        }

        nodeController = new LliseNodeController();
        LliseAlgorithmState state = new LliseAlgorithmState(origin);

        origin.fixNodeIDs();
        currentNodeID = origin.nodeList.size()-1;

        state.nodeState = ((LliseNodeAlgorithmState)nodeController.init(origin,currentNodeID));
        state.nodeState.tSpannerMeasure = tMeasure;
        state.phase = RUNNING;

        return state;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseAlgorithmState currentState = (LliseAlgorithmState)algorithmState;
        if(nodeController.isFinished(currentState.nodeState)){

            if(isFinished(currentState)){
                currentState.phase = FINISHED;
            }else {
                currentNodeID -= 1;
                LinkedList<Edge> tmpEdges = new LinkedList<>();
                tmpEdges.addAll(currentState.nodeState.edgesChosen);


                currentState.nodeState = (LliseNodeAlgorithmState) nodeController.init(currentState.nodeState.origin, currentNodeID);
                currentState.edgesChosen = tmpEdges;
            }
        }else {
            nodeController.processState(currentState.nodeState);
        }

        return currentState;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return currentNodeID <= 0;
    }
}
