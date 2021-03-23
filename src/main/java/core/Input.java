package core;

import util.AWTEventListener;

import javax.security.auth.kerberos.KerberosTicket;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Input implements AWTEventListener {
    private final ConcurrentLinkedQueue<AWTEvent> events = new ConcurrentLinkedQueue<>();
    private final List<EventListener> listeners  = new ArrayList<>();

    public void process() {
        while (!events.isEmpty()) {
            AWTEvent event = events.poll();
            for (var listener : listeners) {
                processEvent(listener, event);
            }
        }
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onEvent(AWTEvent event) {
        events.add(event);
    }

    private void processEvent(EventListener listener, AWTEvent e) {
        if(e instanceof KeyEvent) {
            if(listener instanceof KeyListener) {
                processKeyEvent((KeyListener) listener, (KeyEvent) e);
            }
        } else if(e instanceof MouseEvent) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_DRAGGED -> {
                    if(listener instanceof MouseMotionListener) {
                        processMouseMotionEvent((MouseMotionListener) listener, (MouseEvent) e);
                    }
                }
                case MouseEvent.MOUSE_WHEEL -> {
                    if(listener instanceof MouseWheelListener) {
                        processMouseWheelEvent((MouseWheelListener) listener, (MouseWheelEvent) e);
                    }
                }
                default -> {
                    if(listener instanceof MouseListener) {
                        processMouseEvent((MouseListener) listener, (MouseEvent) e);
                    }
                }
            }
        } else if(listener instanceof AWTEventListener) {
            ((AWTEventListener)listener).onEvent(e);
        }
    }

    private void processKeyEvent(KeyListener listener, KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_TYPED -> listener.keyTyped(e);
            case KeyEvent.KEY_PRESSED -> listener.keyPressed(e);
            case KeyEvent.KEY_RELEASED -> listener.keyReleased(e);
        }
    }

    private void processMouseEvent(MouseListener listener, MouseEvent e) {
        switch (e.getID()) {
            case MouseEvent.MOUSE_PRESSED -> listener.mousePressed(e);
            case MouseEvent.MOUSE_RELEASED -> listener.mouseReleased(e);
            case MouseEvent.MOUSE_CLICKED -> listener.mouseClicked(e);
            case MouseEvent.MOUSE_EXITED -> listener.mouseExited(e);
            case MouseEvent.MOUSE_ENTERED -> listener.mouseEntered(e);
        }
    }

    private void processMouseMotionEvent(MouseMotionListener listener, MouseEvent e) {
        switch (e.getID()) {
            case MouseEvent.MOUSE_MOVED -> listener.mouseMoved(e);
            case MouseEvent.MOUSE_DRAGGED -> listener.mouseDragged(e);
        }
    }

    private void processMouseWheelEvent(MouseWheelListener listener, MouseWheelEvent e) {
        switch(e.getID()) {
            case MouseEvent.MOUSE_WHEEL -> listener.mouseWheelMoved(e);
        }
    }
}