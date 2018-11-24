package Controller;

import View.MainWindow;

import javax.swing.*;
import java.awt.*;

public class MainController {

    public static void main(String[] args){
         JFrame mainFrame = new JFrame();
         MainWindow mainWindow = new MainWindow();
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.setContentPane(mainWindow.mainPanel);


         mainFrame.pack();
         mainFrame.setVisible(true);



    }
}
