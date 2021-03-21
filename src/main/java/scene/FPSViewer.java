package scene;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

public class FPSViewer extends Entity {
    private int ticks = 0;
    private int frames = 0;

    private int averageTicks = 0;
    private int averageFrames = 0;

    private long lastSecondTimeMs = 0;

    public FPSViewer(Scene scene) {
        super(scene);
    }

    @Override
    public void render(Graphics2D g) {
        frames++;

        g.setTransform(new AffineTransform());
        g.setColor(Color.black);
        g.drawString("FPS: " + averageFrames + " Updates: " + averageTicks, 10, 20);
    }

    @Override
    public void update() {
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
}
