package View;

import View.Shapes.Node;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow{

    private boolean drawNode = false;

    public MainWindow() {
        MainWindow window = this;

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

        this.newNodeButton.addActionListener(e -> {
            window.drawNode = true;
        });
    }

    void addNodeClickExample(Node n) {
        MainWindow window = this;
        n.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                window.debugText.append("Node clicked.");
            }
        });
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JTextArea debugText;


}
