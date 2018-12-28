package View;

import Model.Graph;
import View.Shapes.Edge;
import View.Shapes.Node;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainWindow{

    private boolean drawNode = false;
    private Node dragged;

    public MainWindow() {
        MainWindow window = this;

        // test code reading a graph

        try {
            Graph graph = Graph.readFile("./test_graph.txt");
            for (Model.Node node : graph.nodeList) {
                drawPanel.shapes.add(new Node(node.x, node.y));
            }
            for (Model.Edge edge: graph.edgeList) {
                drawPanel.shapes.add(new Edge(edge.left.x, edge.left.y, edge.right.x, edge.right.y));
            }
        }
        catch (IOException ignored) {

        }

        // run algorithm here

        // the following code belongs into a dedicated controller

        // example code for adding nodes
        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (window.drawNode) {
                    Node node = new Node(e.getX(), e.getY());
                    drawPanel.shapes.add(node);

                    // example code for adding click handler to node
                    window.addNodeClickExample(node);

                    window.drawNode = false;
                }
            }
        });

        this.drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (window.dragged != null) {
                    window.dragged.updateCoordinates(e.getX(), e.getY());
                }
            }
        });

        this.newNodeButton.addActionListener(e -> {
            window.drawNode = true;
        });
    }

    void addNodeClickExample(Node n) {

        MainWindow window = this;
        n.addMouseListener(new MouseAdapter() {
            // click example
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                window.debugText.append("Node clicked.");
            }

            //dragging example
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                window.dragged = n;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                window.dragged = null;
            }
        });
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JTextArea debugText;


}
