package View;

import View.Shapes.Node;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow{

    private boolean drawNode = false;

    public MainWindow() {
        MainWindow window = this;
        this.drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (window.drawNode) {
                    drawPanel.shapes.add(new Node(e.getX(), e.getY()));
                    window.drawNode = false;
                }
            }
        });

        this.newNodeButton.addActionListener(e -> {
            window.drawNode = true;
        });
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JTextArea debugText;


}
