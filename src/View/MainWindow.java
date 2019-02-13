package View;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Controller.LiseAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainWindow{

    private boolean drawNode = false;
    private boolean drawRadius = false;
    private View.Shapes.Node drawnNode = null;
    private View.Shapes.Radius drawnRadius = null;
    private int drawnX, drawnY;

    private View.Shapes.Node dragged;

    private GraphDrawer graphDrawer;
    private Graph currentGraph;

    public MainWindow() {

        graphDrawer = new GraphDrawer(drawPanel);
        currentGraph = new Graph();

        // reading a graph

//        Graph graph = Graph.readFile("./test_graph.txt");

        setupAlgoChooseAndStart();

        setupAddNode();

        MainWindow window = this;

        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (window.dragged != null) {
                    window.dragged.updateCoordinates(e.getX(), e.getY());
                }
            }
        });
    }

    private void setupAddNode() {
        MainWindow window = this;

        this.newNodeButton.addActionListener(e -> drawNode = true);

        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (drawNode) {
                    drawnX = e.getX();
                    drawnY = e.getY();
                    drawnNode = new View.Shapes.Node(drawnX, drawnY);
                    drawPanel.shapes.add(drawnNode);

                    drawnRadius = new View.Shapes.Radius(drawnX, drawnY, 0);
                    drawPanel.shapes.add(drawnRadius);

                    drawPanel.update();

                    drawNode = false;
                    drawRadius = true;
                } else if (drawRadius) {
                    drawPanel.shapes.remove(drawnNode);
                    drawPanel.shapes.remove(drawnRadius);

                    double radius = sqrt(pow(drawnX - e.getX(), 2) + pow(drawnY - e.getY(), 2));

                    Model.Node modelNode = new Model.Node(drawnX, drawnY, radius);
                    currentGraph.insertNode(modelNode);

                    graphDrawer.draw(currentGraph);

                    drawRadius = false;
                }
            }
        });

        drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (drawRadius) {
                    drawnRadius.radius = sqrt(pow(drawnX - e.getX(), 2) + pow(drawnY - e.getY(), 2));

                    drawPanel.update();
                }
            }
        });
    }

    private void setupAlgoChooseAndStart() {
        MainWindow window = this;
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");

        this.startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String selected = (String)algoChooser.getSelectedItem();
                switch (selected) {
                    case "LIFE":
                        new Thread(new RunDemo(graphDrawer, currentGraph, new LifeAlgorithmController())).start();
                        break;
                    case "LISE":
                        new Thread(new RunDemo(graphDrawer, currentGraph, new LiseAlgorithmController())).start();
                        break;
                }
            }
        });
    }
//
//    void addNodeClickExample(Node n) {
//
//        MainWindow window = this;
//        n.addMouseListener(new MouseAdapter() {
//            // click example
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                window.debugText.append("Node clicked.");
//            }
//
//            //dragging example
//            @Override
//            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
//                window.dragged = n;
//            }
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
//                window.dragged = null;
//            }
//        });
//    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JTextArea debugText;
    private JComboBox algoChooser;
    private JButton startButton;


}
