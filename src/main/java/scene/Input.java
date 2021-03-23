package scene;

import util.AWTEventListener;

import javax.security.auth.kerberos.KerberosTicket;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Input implements AWTEventListener {
    ConcurrentLinkedQueue<AWTEvent> events;
    List<EventListener> listenerList;

    public Input() {
        events = new ConcurrentLinkedQueue<AWTEvent>();
        listenerList = new ArrayList<>();

    }

    public void process() {
        AWTEvent event;
        while (events.peek() != null) {
            event = events.poll();
            int id = event.getID();
            for (EventListener listener : listenerList) {
                if (listener instanceof KeyListener) {
                    if (event instanceof KeyListener) {
                        switch (id) {
                            case KeyEvent.KEY_TYPED:
                                ((KeyListener) listener).keyTyped((KeyEvent) event);
                                break;
                            case KeyEvent.KEY_PRESSED:
                                ((KeyListener) listener).keyPressed((KeyEvent) event);
                                break;
                            case KeyEvent.KEY_RELEASED:
                                ((KeyListener) listener).keyReleased((KeyEvent) event);
                                break;
                        }
                    }
                } else if (listener instanceof MouseListener) {
                    if (event instanceof MouseListener) {
                        switch (id) {
                            case MouseEvent.MOUSE_PRESSED:
                                ((MouseListener) listener).mousePressed((MouseEvent) event);
                                break;
                            case MouseEvent.MOUSE_RELEASED:
                                ((MouseListener) listener).mouseReleased((MouseEvent) event);
                                break;
                            case MouseEvent.MOUSE_CLICKED:
                                ((MouseListener) listener).mouseClicked((MouseEvent) event);
                                break;
                            case MouseEvent.MOUSE_EXITED:
                                ((MouseListener) listener).mouseExited((MouseEvent) event);
                                break;
                            case MouseEvent.MOUSE_ENTERED:
                                ((MouseListener) listener).mouseEntered((MouseEvent) event);
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(AWTEvent event) {
        events.add(event);
    }
}
