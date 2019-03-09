package View;

import Model.Graph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class EditorForm {

    private boolean drawNode = false;
    private boolean drawRadius = false;
    private Model.Node drawnNode = null;
    private int drawnX, drawnY;

    private View.Shapes.Radius hoverRadius;

    public GraphDrawer graphDrawer;
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
        setupSaveLoad();
        setUpHeatmapControl();
    }

    private void setupSaveLoad() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(new FileNameExtensionFilter(
                "Graph Text File", "txt"));

        this.saveButton.addActionListener(e -> {
            int returnVal = chooser.showSaveDialog(this.mainPanel);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String fileName = chooser.getSelectedFile().getPath();
                    if (!fileName.endsWith(".txt")) {
                        fileName += ".txt";
                    }
                    Graph.writeFile(currentGraph, fileName);
                } catch (IOException ex) {
                    System.out.println("Could not save file.");
                }
            }
        });

        this.loadButton.addActionListener(e -> {
            int returnVal = chooser.showOpenDialog(this.mainPanel);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    currentGraph = Graph.readFile(chooser.getSelectedFile().getPath());
                    graphDrawer.draw(currentGraph, this.radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
                } catch (IOException ex) {
                    System.out.println("Could not read file.");
                }
            }
        });
    }

    private void setupRadiiControl() {
        this.radiiRadioButton.addActionListener(e -> {
            graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
        });
    }

    private void setUpHeatmapControl(){
        this.heatmapRadioButton.addActionListener(e->{
            graphDrawer.draw(currentGraph,radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
        });
    }

    private void setupNodeDragging() {
        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (graphDrawer.draggedNode != null) {
                    graphDrawer.draggedNode.x = e.getX();
                    graphDrawer.draggedNode.y = e.getY();
                    currentGraph.updateNeighbours(graphDrawer.draggedNode);
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
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
                    n.radius = sqrt(pow(n.x - e.getX(), 2) + pow(n.y - e.getY(), 2));
                    currentGraph.updateNeighbours(n);
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
                    graphDrawer.drawNode(n, true, Color.BLACK);
                    drawPanel.update();
                }
            }
        });
    }

    private void setupAddRemoveNode() {
        this.newNodeButton.addActionListener(e -> drawNode = true);

        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (drawNode) {
                    drawnX = e.getX();
                    drawnY = e.getY();
                    drawnNode = new Model.Node(drawnX, drawnY, 0);
                    currentGraph.insertNode(drawnNode);

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
                    drawNode = false;
                    drawRadius = true;
                } else if (drawRadius) {
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
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
                    drawnNode.radius = radius;
                    currentGraph.updateNeighbours(drawnNode);

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
                    graphDrawer.drawNode(drawnNode, true, Color.BLACK);

                    drawPanel.update();
                }
            }
        });

        this.clearButton.addActionListener(e -> {
            this.currentGraph = new Graph();
            this.graphDrawer.draw(this.currentGraph, radiiRadioButton.isSelected(),heatmapRadioButton.isSelected());
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
//                        new Thread(new AlgorithmRunner(algorithmDrawer, currentGraph, new LifeAlgorithmController())).start();
//                        break;
//                    case "LISE":
//                        new Thread(new AlgorithmRunner(algorithmDrawer, currentGraph, new LiseAlgorithmController())).start();
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
    private JButton loadButton;
    private JButton saveButton;
    private JRadioButton heatmapRadioButton;
}
