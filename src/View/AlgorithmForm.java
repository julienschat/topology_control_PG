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
        setupAlgoChooser();

    }

    public void setupReloadButton(){
        this.reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGraph = editor.currentGraph.cloneGraphWithEdges();
                drawPanel.shapes.clear();
                algorithmDrawer.draw(currentGraph, true,Color.black);

            }
        });
    }

    public void setupStartButton(){

    }
    public void setupStepButton(){
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithmRunning){
                    algorithmController.processState(algorithmState);
                    algorithmDrawer.drawAlgorithmState(algorithmState);
                }else {
                    switch ((String) algoChooser.getSelectedItem()) {
                        case "LIFE":
                            algorithmController = new LifeAlgorithmController();
                            break;
                        case "LISE":
                            algorithmController = new LiseAlgorithmController();
                            break;
                    }
                    algorithmState = algorithmController.init(currentGraph);
                    algorithmDrawer.drawAlgorithmState(algorithmState);
                    algorithmRunning = true;
                }
            }
        });
    }

    public void setupStopButton(){

    }

    public void setupAlgoChooser(){
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                switch((String)algoChooser.getSelectedItem()){
                    case "LIFE":
                        new Thread(new AlgorithmRunner(algorithmDrawer,currentGraph,new LifeAlgorithmController())).start();
                        break;
                    case "LISE":
                        new Thread(new AlgorithmRunner(algorithmDrawer,currentGraph,new LiseAlgorithmController())).start();
                        break;
                }
            }
        });

    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JComboBox algoChooser;
    private JButton stopButton;
    private JButton startButton;
    private JButton stepButton;
    private JButton reloadButton;
    private JTextField tSpan;
}
