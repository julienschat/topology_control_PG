package View;

import Controller.AlgorithmController;
import Controller.AlgorithmRunner;
import Controller.LifeAlgorithmController;
import Controller.LiseAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AlgorithmForm {
    EditorForm editor;
    AlgorithmDrawer algorithmDrawer;
    Graph currentGraph;
    boolean algorithmRunning = false;
    AlgorithmState algorithmState;
    AlgorithmController algorithmController;

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

    public void setupReloadButton(){
        this.reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGraph = editor.currentGraph.cloneGraphWithEdges();
                drawPanel.shapes.clear();
                algorithmDrawer.draw(currentGraph, true,Color.black);
                algorithmRunning = false;

            }
        });
    }

    public void setupStepButton(){
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithmRunning){
                    algorithmState = algorithmController.next(algorithmState);
                    algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected());
                }else {
                    switch ((String) algoChooser.getSelectedItem()) {
                        case "LIFE":
                            algorithmController = new LifeAlgorithmController();
                            algorithmState = algorithmController.init(currentGraph);
                            break;
                        case "LISE":
                            algorithmController = new LiseAlgorithmController();
                            algorithmState = algorithmController.init(currentGraph,((Number)(tSpanChooser.getModel().getValue())).doubleValue());
                            break;
                    }

                    algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected());
                    algorithmRunning = true;
                }
            }
        });
    }

    public void setupStopButton(){

    }

    public void setupBackButton() {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (algorithmRunning) {
                    algorithmState = algorithmController.back(algorithmState);
                    algorithmDrawer.drawAlgorithmState(algorithmState, heatmapRadioButton.isSelected());
                }
            }
        });
    }

    public void setupStartButton(){
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch((String)algoChooser.getSelectedItem()){
                    case "LIFE":
                        new Thread(new AlgorithmRunner(algorithmDrawer,currentGraph,new LifeAlgorithmController())).start();
                        break;
                    case "LISE":
                        AlgorithmRunner runner = new AlgorithmRunner(algorithmDrawer,currentGraph,new LiseAlgorithmController());
                        runner.settSpannerMeasure(((Number)(tSpanChooser.getModel().getValue())).doubleValue());
                        new Thread(runner).start();
                        break;
                }
            }
        });
    }

    public void setupTSpanChooser() {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setStepSize(1);
        model.setValue(1);
        tSpanChooser.setModel(model);
    }

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
}
