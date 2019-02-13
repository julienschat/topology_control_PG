package Controller;

import View.EditorForm;

import javax.swing.*;

public class MainController {

    public static void main(String[] args){
         JFrame mainFrame = new JFrame();
         EditorForm editorForm = new EditorForm();
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.setContentPane(editorForm.mainPanel);

         mainFrame.pack();
         mainFrame.setVisible(true);
    }
}


