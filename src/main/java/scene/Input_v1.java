package scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.http.WebSocket;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class Input_v1 implements KeyListener {

    ConcurrentLinkedQueue<KeyEvent> keyEvents;
    List<KeyListener> keyListenerList;

    public Input_v1() {
        keyEvents = new ConcurrentLinkedQueue<KeyEvent>();
        keyListenerList = new ArrayList<KeyListener>();
        addKeyListener(this);
    }

    public void process() {
        while (keyEvents.peek() != null) {
            KeyEvent tmpEvent = keyEvents.poll();
            for (KeyListener listener : keyListenerList) {
                listener.keyPressed(tmpEvent);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyEvents.add(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
