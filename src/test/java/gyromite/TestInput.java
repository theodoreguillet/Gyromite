package gyromite;

import org.junit.Test;

import gyromite.core.Input;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.event.*;

public class TestInput implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final Input input = new Input();
    private final Component source = new Component() {
        @Override
        public String getName() {
            return super.getName();
        }
    };

    boolean keyTyped = false;
    boolean keyPressed = false;
    boolean keyReleased = false;
    boolean mouseClicked = false;
    boolean mousePressed = false;
    boolean mouseReleased = false;
    boolean mouseEntered = false;
    boolean mouseExited = false;
    boolean mouseDragged = false;
    boolean mouseMoved = false;
    boolean mouseWheelMoved = false;

    @Test
    public void test() {
        input.addListener(this);

        input.onEvent(new KeyEvent(source, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, 'A'));
        input.onEvent(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_A, 'A'));
        input.onEvent(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_A, 'A'));

        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 0, false, 1));
        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 0, false, 1));
        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, 0, false, 1));
        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, 0, false, 1));
        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, 0, false, 1));

        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_DRAGGED, 0, 0, 0, 0, 0, false, 1));
        input.onEvent(new MouseEvent(source, MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, 0, false, 1));

        input.onEvent(new MouseWheelEvent(source, MouseEvent.MOUSE_WHEEL, 0, 0, 0, 0, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1));

        input.process();

        assertTrue(keyTyped);
        assertTrue(keyPressed);
        assertTrue(keyReleased);
        assertTrue(mouseClicked);
        assertTrue(mousePressed);
        assertTrue(mouseReleased);
        assertTrue(mouseEntered);
        assertTrue(mouseExited);
        assertTrue(mouseDragged);
        assertTrue(mouseMoved);
        assertTrue(mouseWheelMoved);

    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyTyped = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyReleased = true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseClicked = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseReleased = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseEntered = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseExited = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDragged = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMoved = true;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelMoved = true;
    }
}
