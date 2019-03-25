package Controller;

import Model.AlgorithmState;
import Model.Graph;
import Model.LliseNodeAlgorithmState;
import Model.Node;

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

        for(Node node : origin.nodeList){

            LliseNodeAlgorithmState state = (LliseNodeAlgorithmState)nodeController.init(origin,node.id);
            state.tSpannerMeasure = tMeasure;
            states.add(state);

        }
        return null;
    }

    @Override
    protected AlgorithmState processState(AlgorithmState algorithmState) {
        for(LliseNodeAlgorithmState state : states){
            state = (LliseNodeAlgorithmState)nodeController.processState(state);
        }
        return null;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return false;
    }
}
