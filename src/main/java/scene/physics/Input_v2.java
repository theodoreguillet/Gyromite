package scene.physics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;
import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;


public class Input_v2 implements KeyListener, MouseListener {
    ConcurrentLinkedQueue<EventObject> events;
    List<EventListener> listenerList;

    public Input_v2() {
        events = new ConcurrentLinkedQueue<EventObject>();
        listenerList = new ArrayList<EventListener>();
        addKeyListener(this);
        addMouseListener(this);
    }

    public void process() {
        while(events.peek() != null) {
            EventObject tmpEvent = events.peek();
            for(EventListener listener : listenerList) {
                if(tmpEvent instanceof KeyEvent) {
                    if(listener instanceof KeyListener) {
                        ((KeyListener) listener).keyPressed((KeyEvent) tmpEvent);
                    }
                }else if(tmpEvent instanceof MouseEvent) {
                    if(listener instanceof MouseListener) {
                        ((MouseListener) listener).mousePressed((MouseEvent) tmpEvent);
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        events.add(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        events.add(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
