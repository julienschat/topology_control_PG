package View;

import Controller.AlgorithmController;
import Controller.AlgorithmRunner;
import Controller.LifeAlgorithmController;
import Controller.LiseAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlgorithmForm {
    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JComboBox algoChooser;
    private JButton stopButton;
    private JButton startButton;
    private JButton stepButton;
    private JButton reloadButton;
    private JTextField tSpan;
    private JButton backButton;
    private JRadioButton heatmapRadioButton;
    private JSpinner tSpanChooser;

    private EditorForm editor;
    private AlgorithmDrawer algorithmDrawer;
    private Graph currentGraph;
    private boolean algorithmRunning = false;
    private AlgorithmState algorithmState;
    private AlgorithmController algorithmController;
    public AtomicBoolean threadRunning = new AtomicBoolean(false);
    private final Object stateLock = new Object();

    public AlgorithmForm(EditorForm editor) {
        super();
        this.editor = editor;
        algorithmDrawer = new AlgorithmDrawer(drawPanel);
        currentGraph = editor.currentGraph.cloneGraphWithEdges();
        algorithmDrawer.draw(currentGraph,true, Color.black);

        setupReloadButton();
        setupStartButton();
        setupStepButton();
        setupStopButton();
        setupBackButton();
        setupTSpanChooser();
    }

    private void setupReloadButton(){
        this.reloadButton.addActionListener(e -> {
            currentGraph = editor.currentGraph.cloneGraphWithEdges();
            drawPanel.shapes.clear();
            algorithmDrawer.draw(currentGraph, true,Color.black);
            algorithmRunning = false;

        });
    }

    private void setupStepButton(){
        stepButton.addActionListener(e -> {
            if(algorithmRunning){
                setState(algorithmController.next(algorithmState));
            } else {
                switch ((String) algoChooser.getSelectedItem()) {
                    case "LIFE":
                        algorithmController = new LifeAlgorithmController();
                        setState(algorithmController.init(currentGraph));
                        break;
                    case "LISE":
                        algorithmController = new LiseAlgorithmController();
                        setState(algorithmController.init(currentGraph,((Number)(tSpanChooser.getModel().getValue())).doubleValue()));
                        break;
                }

                algorithmRunning = true;
            }
        });
    }

    private void setupStopButton(){
        stopButton.addActionListener(e -> threadRunning.set(false));
    }

    private void setupBackButton() {
        backButton.addActionListener(e -> {
            if (algorithmRunning) {
                algorithmState = algorithmController.back(algorithmState);
                algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected());
            }
        });
    }

    private void setupStartButton(){
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");

        startButton.addActionListener(e -> {
            if (!algorithmRunning) {
                algorithmRunning = true;

                switch((String)algoChooser.getSelectedItem()){
                    case "LIFE":
                        algorithmController = new LifeAlgorithmController();
                        setState(algorithmController.init(currentGraph));
                        break;
                    case "LISE":
                        algorithmController = new LiseAlgorithmController();
                        setState(algorithmController.init(currentGraph, (double)tSpanChooser.getModel().getValue()));
                        break;
                }
            }

            threadRunning.set(true);
            new Thread(new AlgorithmRunner(this, algorithmController, algorithmState)).start();
        });
    }

    private void setupTSpanChooser() {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setStepSize(1);
        model.setValue(1);
        tSpanChooser.setModel(model);
    }

    public void setState(AlgorithmState state) {
        synchronized (stateLock) {
            this.algorithmState = state;
            algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected());
        }
    }
}
