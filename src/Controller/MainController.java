package Controller;

import Model.Graph;
import View.EditorForm;

import javax.swing.*;
import java.io.IOException;

public class MainController {

    public static void main(String[] args){
         JFrame mainFrame = new JFrame();
         EditorForm editorForm = new EditorForm(mainFrame);
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.setContentPane(editorForm.mainPanel);
         mainFrame.pack();
         mainFrame.setVisible(true);

         // For faster testing:
        try {
            Graph currentGraph = Graph.readFile("graph1.txt");
            editorForm.currentGraph = currentGraph;
            editorForm.graphDrawer.draw(currentGraph, false,false, false);
        }catch(IOException ex){
            System.out.println("Could not load graph");
        }
    }
}


