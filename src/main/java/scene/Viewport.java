package scene;

import util.AWTEventListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

public class Viewport extends Canvas {
    private final ArrayList<AWTEventListener> eventListeners = new ArrayList<>();


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
