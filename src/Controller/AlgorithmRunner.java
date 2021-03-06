package Controller;

import Model.AlgorithmState;
import View.AlgorithmForm;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AlgorithmRunner is a Runnable, running the chosen algorithm automatically till it's finished or the thread is interrupted.
 */
public class AlgorithmRunner implements Runnable {
    private AlgorithmState state;
    private int sleep;
    private final AlgorithmForm algoForm;
    private final AlgorithmController algorithmController;

    public AlgorithmRunner(AlgorithmForm algoForm, AlgorithmController algorithmController, AlgorithmState state, int sleep) {
        this.algoForm = algoForm;
        this.algorithmController = algorithmController;
        this.state = state;
        this.sleep = sleep;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(this.sleep); // have sleep two times to ask running.get() right before next execution

            // running algorithm stepwise
            while (!algorithmController.isFinished(state) && algoForm.threadRunning.get()) {
                state = algorithmController.next(state);
                algoForm.setState(state);
                TimeUnit.MILLISECONDS.sleep(this.sleep);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
