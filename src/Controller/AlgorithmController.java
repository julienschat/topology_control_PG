package Controller;

abstract public class AlgorithmController {


    public abstract Model.AlgorithmState Init(Model.Graph origin);

    public abstract void processState(Model.AlgorithmState algorithmState);
}
