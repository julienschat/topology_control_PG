package View;

import Model.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class EditorForm {

    private boolean drawNode = false;
    private boolean drawRadius = false;
    private Model.Node drawnNode = null;
    private int drawnX, drawnY;

    private View.Shapes.Radius hoverRadius;

    private GraphDrawer graphDrawer;
    public Graph currentGraph;

    public EditorForm() {

        graphDrawer = new GraphDrawer(drawPanel);
        currentGraph = new Graph();

        // reading a graph

//        Graph graph = Graph.readFile("./test_graph.txt");

        setupAlgoChooseAndStart();
        setupAddRemoveNode();
        setupNodeDragging();
        setupRadiiControl();
        setupRadiusChange();
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

    private void setupRadiusChange() {
        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (graphDrawer.draggedRadiusNode != null) {
                    Model.Node n = graphDrawer.draggedRadiusNode;
                    currentGraph.updateRadius(n, sqrt(pow(n.x - e.getX(), 2) + pow(n.y - e.getY(), 2)));
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
                }
            }
        });
    }

    private void setupAddRemoveNode() {
        EditorForm window = this;

        this.newNodeButton.addActionListener(e -> drawNode = true);

        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (drawNode) {
                    drawnX = e.getX();
                    drawnY = e.getY();
                    Model.Node modelNode = new Model.Node(drawnX, drawnY, 0);
                    currentGraph.insertNode(modelNode);

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());

                    drawNode = false;
                    drawRadius = true;
                } else if (drawRadius) {
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
                    double radius = sqrt(pow(drawnX - e.getX(), 2) + pow(drawnY - e.getY(), 2));
                    currentGraph.updateRadius(drawnNode, radius);

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());

                    View.Shapes.Radius highlight = new View.Shapes.Radius(drawnX, drawnY, radius);
                    highlight.color = Color.BLUE;
                    drawPanel.shapes.add(highlight);
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
        //algoChooser.addItem("LIFE");
        //algoChooser.addItem("LISE");

        EditorForm editor = this;

        this.startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                JFrame frame = new JFrame();
                AlgorithmForm algorithmForm = new AlgorithmForm(editor);
                frame.setContentPane(algorithmForm.mainPanel);

                frame.pack();
                frame.setVisible(true);

//                String selected = (String)algoChooser.getSelectedItem();
//                switch (selected) {
//                    case "LIFE":
//                        new Thread(new AlgorithmRunner(graphDrawer, currentGraph, new LifeAlgorithmController())).start();
//                        break;
//                    case "LISE":
//                        new Thread(new AlgorithmRunner(graphDrawer, currentGraph, new LiseAlgorithmController())).start();
//                        break;
//                }
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
