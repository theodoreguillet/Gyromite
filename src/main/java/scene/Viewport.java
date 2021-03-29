package scene;

import util.AWTEventListener;

import java.awt.Canvas;
import java.awt.AWTEvent;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * The view into the screen.
 */
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

    /**
     * Add a listener.
     * @param listener Listen {@link AWTEvent} from the {@link Canvas}
     */
    public void addEventListener(AWTEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Remove a listener.
     * @param listener The listener to remove
     */
    public void removeEventListener(AWTEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public void paint(Graphics g) { }
}
