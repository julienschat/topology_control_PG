package Controller;

abstract public class AlgorithmController {


    public abstract Model.AlgorithmState Init(Model.Graph pristine);

    public abstract void processState(Model.AlgorithmState algorithmState);
}
