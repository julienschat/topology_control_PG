package Controller;

import DataStructures.Edge;
import DataStructures.Graph;
import Model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static Model.LliseAlgorithmPhase.*;

public class LliseAlgorithmController extends AlgorithmController{
    private LliseNodeController nodeController;
    private double tMeasure;

    @Override
    public AlgorithmState init(Graph origin) {
        nodeController = new LliseNodeController();
        LliseAlgorithmState state = new LliseAlgorithmState(origin);

        origin.fixNodeIDs();
        state.currentNodeID = origin.nodeList.size()-1;

        nodeController.setNode(origin.getNodeById(state.currentNodeID));
        state.nodeState = ((LliseNodeAlgorithmState)nodeController.init(origin));
        state.nodeState.tSpannerMeasure = tMeasure;
        state.phase = RUN_PARALLEL;

        return state;
    }

    public void setTMeasure(double t) {
        this.tMeasure = t;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        LliseAlgorithmState currentState = (LliseAlgorithmState)algorithmState;
        if (currentState.phase == RUN_PARALLEL) {
            if (nodeController.isFinished(currentState.nodeState)) {
                currentState.edgesChosenByNodes.add(currentState.nodeState.edgesChosen);
                if (currentState.currentNodeID <= 0) {
                    currentState.edgesChosen = new LinkedList<>();
                    currentState.phase = MERGING;
                } else {
                    currentState.currentNodeID -= 1;
                    LinkedList<Edge> tmpEdges = new LinkedList<>();
//                    tmpEdges.addAll(currentState.nodeState.edgesChosen);
                    double tSpannerMeasure = currentState.nodeState.tSpannerMeasure;

                    nodeController.setNode(currentState.nodeState.origin.getNodeById(currentState.currentNodeID));
                    currentState.nodeState = (LliseNodeAlgorithmState) nodeController.init(currentState.nodeState.origin);
                    currentState.nodeState.tSpannerMeasure = tSpannerMeasure;
//                    currentState.edgesChosen = tmpEdges;
                }
            } else {
                nodeController.processState(currentState.nodeState);
            }
        } else if (currentState.phase == MERGING) {
            if (currentState.edgesChosenByNodes.isEmpty()) {
                currentState.phase = FINISHED;
            } else {
                List<Edge> newEdges = currentState.edgesChosenByNodes.pop().stream()
                        .filter(e -> !currentState.edgesChosen.contains(e)).collect(Collectors.toList());
                currentState.edgesChosen.addAll(newEdges);
            }
        }

        return currentState;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LliseAlgorithmState)algorithmState).phase == LliseAlgorithmPhase.FINISHED;
    }

    @Override
    public String getPhaseDescription(AlgorithmState _state) {
        LliseAlgorithmState state = (LliseAlgorithmState)_state;
        switch (state.phase) {
            case RUN_PARALLEL:
                return String.format("Node %d: %s", state.currentNodeID, nodeController.getPhaseDescription(state.nodeState));
            case MERGING:
                return "Merging results";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
