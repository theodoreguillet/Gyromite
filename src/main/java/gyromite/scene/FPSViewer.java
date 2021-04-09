package gyromite.scene;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 * Show rendering and update speed on the screen.
 */
public class FPSViewer extends Node {
    private int ticks = 0;
    private int frames = 0;

    private int averageTicks = 0;
    private int averageFrames = 0;

    private long lastSecondTimeMs = 0;

    private Color color = Color.BLACK;

    public FPSViewer() {
        super();
    }

    public Color color() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void update() {
        super.update();

        ticks++;

        long timeMs = System.currentTimeMillis();
        if(timeMs - lastSecondTimeMs >= 1000) {
            lastSecondTimeMs = timeMs;
            averageFrames = frames;
            averageTicks = ticks;
            frames = 0;
            ticks = 0;
        }
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        frames++;

        g.setTransform(new AffineTransform());
        g.setColor(color);
        g.drawString("FPS: " + averageFrames + " Updates: " + averageTicks, 10, 20);
    }
}
