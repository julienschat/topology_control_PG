package Controller;

import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;

public class LifeAlgorithmController extends AlgorithmController {
    @Override
    public AlgorithmState Init(Graph pristine) {
        return new LifeAlgorithmState(pristine);
    }

    @Override
    public void processState(AlgorithmState algorithmState) {
        
    }
}
