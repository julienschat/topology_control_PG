package View;

import javax.swing.*;

public class MainWindow{

    public MainWindow() {
        this.newNodeButton.addActionListener(e -> {
            this.debugText.append("+");
        });
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JButton newNodeButton;
    private JTextArea debugText;


}
