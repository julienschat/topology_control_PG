package View;

import Controller.AlgorithmController;
import Controller.LifeAlgorithmController;
import Model.AlgorithmState;
import Model.Graph;
import Model.LifeAlgorithmState;
import View.Shapes.Edge;
import View.Shapes.Node;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainWindow{

    private boolean drawNode = false;
    private Node dragged;

    public MainWindow() {
        MainWindow window = this;

        new Thread(new RunDemo(drawPanel)).start();

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
