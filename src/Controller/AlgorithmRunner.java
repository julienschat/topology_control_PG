package Controller;

import Model.AlgorithmState;
import View.AlgorithmForm;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlgorithmRunner implements Runnable {
    private AlgorithmState state;
    private final AlgorithmForm algoForm;
    private final AlgorithmController algorithmController;

    public AlgorithmRunner(AlgorithmForm algoForm, AlgorithmController algorithmController, AlgorithmState state) {
        this.algoForm = algoForm;
        this.algorithmController = algorithmController;
        this.state = state;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(400); // have sleep two times to ask running.get() right before next execution

            // running algorithm stepwise
            while (!algorithmController.isFinished(state) && algoForm.threadRunning.get()) {
                state = algorithmController.next(state);
                algoForm.setState(state);
                TimeUnit.MILLISECONDS.sleep(400);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
