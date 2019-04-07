package View;

import Controller.*;
import Model.AlgorithmState;
import DataStructures.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private JLabel statusText;
    private JRadioButton coverageRadioButton;
    private JButton fastForwardButton;

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
        loadGraph();

        setupReloadButton();
        setupStartButton();
        setupStepButton();
        setupStopButton();
        setupBackButton();
        setupFastForwardButton();
        setupTSpanChooser();
        setUpRadioControls();
    }

    private void loadGraph() {
        currentGraph = editor.currentGraph.cloneGraphWithEdges();
        algorithmDrawer.updateScaling(currentGraph.edgeList);
        drawPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                algorithmDrawer.updateScaling(currentGraph.edgeList);
            }
        });
        drawPanel.shapes.clear();
        currentGraph.calculateCoverages();
        algorithmDrawer.draw(currentGraph, Color.black, heatmapRadioButton.isSelected(), coverageRadioButton.isSelected());
        algorithmRunning = false;
        algorithmState = null;
        threadRunning.set(false);
        statusText.setText("Ready");
    }

    private void setUpRadioControls(){
        ActionListener update = e->{
            if (algorithmState == null) {
                algorithmDrawer.draw(currentGraph, Color.black, heatmapRadioButton.isSelected(), coverageRadioButton.isSelected());
            } else {
                algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected(), coverageRadioButton.isSelected());
            }
        };
        this.heatmapRadioButton.addActionListener(update);
        this.coverageRadioButton.addActionListener(update);
    }

    private void setupReloadButton(){
        this.reloadButton.addActionListener(e -> {
            loadGraph();
        });
    }

    private void setupStepButton(){
        stepButton.addActionListener(e -> {
            if(algorithmRunning){
                setState(algorithmController.next(algorithmState));
            } else {
                initAlgorithmController();
                algorithmRunning = true;
            }
        });
    }

    private void setupFastForwardButton() {
        fastForwardButton.addActionListener(e -> {
            if (!algorithmRunning) {
                algorithmRunning = true;
                initAlgorithmController();
            }

            while (!algorithmController.isFinished(algorithmState)) {
                algorithmState = algorithmController.next(algorithmState);
            }

            setState(algorithmState);
        });
    }

    private void setupStopButton(){
        stopButton.addActionListener(e -> threadRunning.set(false));
    }

    private void setupBackButton() {
        backButton.addActionListener(e -> {
            if (algorithmRunning) {
                setState(algorithmController.back(algorithmState));
            }
        });
    }

    private void initAlgorithmController() {
        switch((String)algoChooser.getSelectedItem()){
            case "LIFE":
                algorithmController = new LifeAlgorithmController();
                setState(algorithmController.init(currentGraph));
                break;
            case "LISE":
                algorithmController = new LiseAlgorithmController();
                ((LiseAlgorithmController) algorithmController).setTMeasure(((Number)tSpanChooser.getModel().getValue()).doubleValue());
                setState(algorithmController.init(currentGraph));
                break;
            case "LISE (Alt)":
                algorithmController = new LiseAlternativeController();
                ((LiseAlternativeController) algorithmController).setTMeasure(((Number)tSpanChooser.getModel().getValue()).doubleValue());
                setState(algorithmController.init(currentGraph));
                break;
            case "LLISE":
                algorithmController = new LliseAlgorithmController();
                ((LliseAlgorithmController) algorithmController).setTMeasure(((Number)tSpanChooser.getModel().getValue()).doubleValue());
                setState(algorithmController.init(currentGraph));
                break;
        }
    }

    private void setupStartButton(){
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");
        algoChooser.addItem("LISE (Alt)");
        algoChooser.addItem("LLISE");
        startButton.addActionListener(e -> {
            if (!algorithmRunning) {
                algorithmRunning = true;
                initAlgorithmController();
            }

            threadRunning.set(true);
            new Thread(new AlgorithmRunner(this, algorithmController, algorithmState, 400)).start();
        });
    }

    private void setupTSpanChooser() {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(1.0);
        model.setStepSize(0.1);
        model.setValue(1.5);
        tSpanChooser.setModel(model);
    }

    public void setState(AlgorithmState state) {
        synchronized (stateLock) {
            this.algorithmState = state;
            algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected(), coverageRadioButton.isSelected());
            if (state.step == 0) {
                statusText.setText("Algorithm initialized");
            } else if (!algorithmController.isFinished(state)) {
                String phase = algorithmController.getPhaseDescription(state);
                statusText.setText(String.format("Step %d: %s", state.step, phase));
            } else {
                statusText.setText(String.format("Algorithm finished in %d steps", state.step));
            }
        }
    }
}
