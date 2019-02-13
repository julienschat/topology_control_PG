package View;

import Controller.LifeAlgorithmController;
import Controller.LiseAlgorithmController;
import Controller.AlgorithmRunner;
import Model.Graph;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainWindow{

    private boolean drawNode = false;
    private boolean drawRadius = false;
    private View.Shapes.Node drawnNode = null;
    private View.Shapes.Radius drawnRadius = null;
    private int drawnX, drawnY;

    private View.Shapes.Radius hoverRadius;

    private GraphDrawer graphDrawer;
    private Graph currentGraph;

    public MainWindow() {

        graphDrawer = new GraphDrawer(drawPanel);
        currentGraph = new Graph();

        // reading a graph

//        Graph graph = Graph.readFile("./test_graph.txt");

        setupAlgoChooseAndStart();
        setupAddRemoveNode();
        setupNodeDragging();
        setupRadiiControl();
    }

    private void setupRadiiControl() {
        this.radiiRadioButton.addActionListener(e -> graphDrawer.draw(currentGraph, radiiRadioButton.isSelected()));
    }

    private void setupNodeDragging() {
        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (graphDrawer.draggedNode != null) {
                    graphDrawer.draggedNode.x = e.getX();
                    graphDrawer.draggedNode.y = e.getY();
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (graphDrawer.hoveredNode != null && hoverRadius == null) {
                    hoverRadius = new View.Shapes.Radius(graphDrawer.hoveredNode.x, graphDrawer.hoveredNode.y, graphDrawer.hoveredNode.radius);
                    drawPanel.shapes.add(hoverRadius);
                    drawPanel.update();
                } else if (hoverRadius != null) {
                    drawPanel.shapes.remove(hoverRadius);
                    hoverRadius = null;
                    drawPanel.update();
                }
            }
        });
    }

    private void setupAddRemoveNode() {
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

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());

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

        this.clearButton.addActionListener(e -> {
            this.currentGraph = new Graph();
            this.graphDrawer.draw(this.currentGraph, radiiRadioButton.isSelected());
        });
    }

    private void setupAlgoChooseAndStart() {
        algoChooser.addItem("LIFE");
        algoChooser.addItem("LISE");

        this.startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String selected = (String)algoChooser.getSelectedItem();
                switch (selected) {
                    case "LIFE":
                        new Thread(new AlgorithmRunner(graphDrawer, currentGraph, new LifeAlgorithmController())).start();
                        break;
                    case "LISE":
                        new Thread(new AlgorithmRunner(graphDrawer, currentGraph, new LiseAlgorithmController())).start();
                        break;
                }
            }
        });
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JComboBox algoChooser;
    private JButton startButton;
    private JButton clearButton;
    private JRadioButton radiiRadioButton;


}
