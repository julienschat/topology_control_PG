package Controller;

import Model.*;

import java.util.LinkedList;

import static Model.LliseAlgorithmPhase.FINISHED;
import static Model.LliseAlgorithmPhase.RUNNING;

public class LliseAlgorithmController extends AlgorithmController{
    private LliseNodeController nodeController;

    private int currentNodeID;
    private double tMeasure;

    @Override
    public AlgorithmState init(Graph origin) {
        nodeController = new LliseNodeController();
        LliseAlgorithmState state = new LliseAlgorithmState(origin);

        origin.fixNodeIDs();
        currentNodeID = origin.nodeList.size()-1;

        nodeController.setNode(origin.getNodeById(currentNodeID));
        state.nodeState = ((LliseNodeAlgorithmState)nodeController.init(origin));
        state.nodeState.tSpannerMeasure = tMeasure;
        state.phase = RUNNING;

        return state;
    }

    public void setTMeasure(double t) {
        this.tMeasure = t;
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

                nodeController.setNode(currentState.nodeState.origin.getNodeById(currentNodeID));
                currentState.nodeState = (LliseNodeAlgorithmState) nodeController.init(currentState.nodeState.origin);
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

    @Override
    public String getPhaseDescription(AlgorithmState _state) {
        LliseAlgorithmState state = (LliseAlgorithmState)_state;
        switch (state.phase) {
            case RUNNING:
                return String.format("Node %d: %s", currentNodeID, nodeController.getPhaseDescription(state.nodeState));
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
