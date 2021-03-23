package game;

import scene.FPSViewer;
import scene.Scene;

import java.awt.event.*;

public class SceneTestInput extends Scene {
    private Window window;

    public SceneTestInput() {
        super();
    }

    @Override
    protected void preload() {
        // ...
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = new FPSViewer(this);

        input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("keyTyped: " + e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("keyPressed: " + e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("keyReleased: " + e.getKeyChar());
            }
        });
        input().addListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("mouseClicked [%d]: (%d, %d)%n", e.getButton(), e.getPoint().x, e.getPoint().y);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.printf("mousePressed [%d]: (%d, %d)%n", e.getButton(), e.getPoint().x, e.getPoint().y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.printf("mouseReleased [%d]: (%d, %d)%n", e.getButton(), e.getPoint().x, e.getPoint().y);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("mouseEntered");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println("mouseExited");
            }
        });
        input().addListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.printf("mouseDragged [%d]: (%d, %d)%n", e.getButton(), e.getPoint().x, e.getPoint().y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.printf("mouseMoved [%d]: (%d, %d)%n", e.getButton(), e.getPoint().x, e.getPoint().y);
            }
        });
        input().addListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                System.out.printf("mouseWheelMoved: %d%n", e.getWheelRotation());
            }
        });
    }
}
