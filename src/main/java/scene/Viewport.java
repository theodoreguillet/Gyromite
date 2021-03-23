package scene;

import util.AWTEventListener;

import java.awt.Canvas;
import java.awt.AWTEvent;
import java.awt.Graphics;
import java.util.ArrayList;

public class Viewport extends Canvas {
    private final ArrayList<AWTEventListener> eventListeners = new ArrayList<>();

    public Viewport() {
        super();
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        super.processEvent(event);
        for(AWTEventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }

    public void addEventListener(AWTEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(AWTEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public void paint(Graphics g) { }
}
