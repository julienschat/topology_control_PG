package Controller;

abstract public class AlgorithmController {

    public abstract Model.AlgorithmState init(Model.Graph pristine, double... params);

    public abstract Model.AlgorithmState processState(Model.AlgorithmState algorithmState);

    public abstract boolean isFinished(Model.AlgorithmState algorithmState);
}
