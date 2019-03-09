package View;

import Model.Graph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class EditorForm {

    private boolean nodeDrawing = false;
    private boolean radiusDrawing = false;
    private Model.Node drawnNode = null;
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
        setupRadiiControl();
        setupRadiusChange();
        setupSaveLoad();
    }

    private void setupSaveLoad() {
        JFileChooser chooser = new JFileChooser();

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
                        graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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

    private void setupRadiiControl() {
        this.radiiRadioButton.addActionListener(e -> {
            graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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

    private void setupRadiusChange() {
        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (graphDrawer.draggedRadiusNode != null) {
                    Model.Node n = graphDrawer.draggedRadiusNode;
                    n.radius = sqrt(pow(n.x - e.getX(), 2) + pow(n.y - e.getY(), 2));
                    currentGraph.updateNeighbours(n);
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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
                    drawnNode = new Model.Node(drawnX, drawnY, 0);
                    currentGraph.insertNode(drawnNode);

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
                    nodeDrawing = false;
                    radiusDrawing = true;
                } else if (radiusDrawing) {
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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

                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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
                    graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
                    editorForm.deleteNodeButton.setBackground(buttonColor);
                    nodeDeleting = false;
                }
            }
        });

        Action clear = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGraph = new Graph();
                graphDrawer.draw(currentGraph, radiiRadioButton.isSelected());
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
    private JButton deleteNodeButton;
}
