package gyromite.util;

import java.awt.AWTEvent;
import java.util.EventListener;

public interface AWTEventListener extends EventListener {

    void onEvent(AWTEvent event);
}
