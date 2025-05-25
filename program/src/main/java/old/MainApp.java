package old;

import javax.swing.*;

public class MainApp //extends Application(для JavaFx)
{
    public static void main(String[] args)
    {
        System.out.println("Start");
        //Swing
        SwingUtilities.invokeLater(() -> {
            new DBInterface().setVisible(true);
        });
    }
}