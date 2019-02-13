package View;

import javax.swing.*;

public class AlgorithmForm {
    EditorForm editor;
    GraphDrawer graphDrawer;

    public AlgorithmForm(EditorForm editor) {
        super();
        this.editor = editor;
        graphDrawer = new GraphDrawer(drawPanel);
        graphDrawer.draw(editor.currentGraph.cloneGraphWithoutEdges(),true);
    }

    public JPanel mainPanel;
    private DrawPanel drawPanel;
    private JComboBox algoChooser;
    private JButton stopButton;
    private JButton startButton;
    private JButton stepButton;
    private JButton reloadButton;
    private JTextField tSpan;
}
