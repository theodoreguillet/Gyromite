package game;

import scene.Scene;
import scene.Viewport;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

/**
 * The game window.
 */
public class Window
{
    public Window (int width, int height, String title, Scene scene) {
        var dim = new Dimension(width, height);
        Viewport viewport = scene.viewport();


        viewport.setPreferredSize(dim);

        JFrame frame = new JFrame (title);
        frame.setVisible(true);
        frame.add(scene.viewport());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        viewport.setFocusable(true);
        viewport.requestFocus();
    }
}
