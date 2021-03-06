package View;

import DataStructures.Graph;
import DataStructures.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Paths;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * The EditorForm wires the buttons of the Editor Frontend with the corresponding controllers.
 * It interacts heavily with the GraphDrawer to find out which nodes, edges and radii are interacted with.
 */
public class EditorForm {

    private boolean nodeDrawing = false;
    private boolean radiusDrawing = false;
    private Node drawnNode = null;
    private int drawnX, drawnY;
    private boolean nodeDragging = false;
    private boolean radiusDragging = false;
    private boolean nodeDeleting = false;
    private boolean interactionActive = false;

    private View.Shapes.Radius hoverRadius;

    public GraphDrawer graphDrawer;
    public Graph currentGraph;
    private JFrame frame;


    public EditorForm(JFrame frame) {

        graphDrawer = new GraphDrawer(drawPanel);
        currentGraph = new Graph();

        this.frame = frame;

        setupAlgoChooseAndStart();
        setupAddDeleteNodeButton();
        setupNodeDragging();
        setupRadioControls();
        setupRadiusChange();
        setupSaveLoad();
    }

    private void setupSaveLoad() {
        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(Paths.get(".").toAbsolutePath().normalize().toFile());
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Graph Text File", "txt"));

        Action save = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = chooser.showSaveDialog(mainPanel);

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
            }
        };

        Action load = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = chooser.showOpenDialog(mainPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        currentGraph = Graph.readFile(chooser.getSelectedFile().getPath());
                        drawGraph();
                    } catch (IOException ex) {
                        System.out.println("Could not read file.");
                    }
                }
            }
        };

        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('s'), "saveGraph");

        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('l'), "loadGraph");

        this.mainPanel.getActionMap().put("saveGraph", save);

        this.mainPanel.getActionMap().put("loadGraph", load);

        this.saveButton.addActionListener(save);

        this.loadButton.addActionListener(load);
    }

    private void setupRadioControls() {
        ActionListener updateGraph = e -> drawGraph();
        this.radiiRadioButton.addActionListener(updateGraph);
        this.heatmapRadioButton.addActionListener(updateGraph);
        this.coverageRadioButton.addActionListener(updateGraph);
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
                    drawGraph();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (!radiiRadioButton.isSelected()) {
                    if (graphDrawer.hoveredNode != null && hoverRadius == null) {
                        hoverRadius = new View.Shapes.Radius(graphDrawer.hoveredNode.x, graphDrawer.hoveredNode.y, graphDrawer.hoveredNode.radius);
                        drawPanel.shapes.add(hoverRadius);
                        drawPanel.update();
                    } else if (graphDrawer.hoveredNode == null && hoverRadius != null) {
                        drawPanel.shapes.remove(hoverRadius);
                        drawPanel.update();
                        hoverRadius = null;
                    }
                }
            }
        });
    }

    private void drawGraph() {
        if (coverageRadioButton.isSelected()) {
            currentGraph.calculateCoverages();
        }
        graphDrawer.draw(currentGraph, radiiRadioButton.isSelected(), heatmapRadioButton.isSelected(),
                coverageRadioButton.isSelected());
    }

    private void setupRadiusChange() {
        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (graphDrawer.draggedRadiusNode != null) {
                    Node n = graphDrawer.draggedRadiusNode;
                    n.radius = sqrt(pow(n.x - e.getX(), 2) + pow(n.y - e.getY(), 2));
                    currentGraph.updateNeighbours(n);
                    drawGraph();
                    graphDrawer.drawNode(n, true, Color.BLACK);
                    drawPanel.update();
                }
            }
        });
    }

    private void setupAddDeleteNodeButton() {
        EditorForm editorForm = this;
        Color buttonColor = this.newNodeButton.getBackground();

        Action toggleAddNode = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nodeDrawing) {
                    editorForm.newNodeButton.setBackground(Color.GREEN);
                    editorForm.newNodeButton.repaint();
                    nodeDrawing = true;
                } else {
                    if (radiusDrawing) {
                        currentGraph.removeNode(drawnNode);
                        radiusDrawing = false;
                    }
                    editorForm.newNodeButton.setBackground(buttonColor);
                    editorForm.newNodeButton.repaint();
                    nodeDrawing = false;

                    drawGraph();
                }
            }
        };

        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (nodeDrawing) {
                    drawnX = e.getX();
                    drawnY = e.getY();
                    drawnNode = new Node(drawnX, drawnY, 0);
                    currentGraph.insertNode(drawnNode);

                    drawGraph();
                    nodeDrawing = false;
                    radiusDrawing = true;
                } else if (radiusDrawing) {
                    drawGraph();
                    editorForm.newNodeButton.setBackground(buttonColor);
                    editorForm.newNodeButton.repaint();
                    radiusDrawing = false;
                }
            }
        });

        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (radiusDrawing) {
                    double radius = sqrt(pow(drawnX - e.getX(), 2) + pow(drawnY - e.getY(), 2));
                    drawnNode.radius = radius;
                    currentGraph.updateNeighbours(drawnNode);

                    drawGraph();
                    graphDrawer.drawNode(drawnNode, true, Color.BLACK);

                    drawPanel.update();
                }
            }
        });

        Action toggleDeleteNode = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nodeDeleting) {
                    editorForm.deleteNodeButton.setBackground(Color.RED);
                    editorForm.deleteNodeButton.repaint();
                    nodeDeleting = true;
                } else {
                    editorForm.deleteNodeButton.setBackground(buttonColor);
                    editorForm.deleteNodeButton.repaint();
                    nodeDeleting = false;
                }
            }
        };

        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (nodeDeleting && editorForm.graphDrawer.clickedNode != null) {
                    currentGraph.removeNode(editorForm.graphDrawer.clickedNode);
                    drawGraph();
                    editorForm.deleteNodeButton.setBackground(buttonColor);
                    nodeDeleting = false;
                }
            }
        });

        Action clear = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGraph = new Graph();
                drawGraph();
            }
        };

        this.clearButton.addActionListener(clear);

        this.newNodeButton.addActionListener(toggleAddNode);

        this.deleteNodeButton.addActionListener(toggleDeleteNode);

        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('n'), "newNode");

        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('d'), "deleteNode");

        this.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('c'), "clearNodes");

        this.mainPanel.getActionMap().put("newNode", toggleAddNode);

        this.mainPanel.getActionMap().put("deleteNode", toggleDeleteNode);

        this.mainPanel.getActionMap().put("clearNodes", clear);
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
    private JButton deleteNodeButton;
    private JRadioButton coverageRadioButton;
}
