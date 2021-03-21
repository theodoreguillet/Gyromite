package game;

import scene.Scene;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Window
{
    public Window (int width, int height, String title, Scene scene) {
        var dim = new Dimension(width, height);
        scene.viewport().setPreferredSize(dim);

        JFrame frame = new JFrame (title);
        frame.add(scene.viewport());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible (true);
    }
}
