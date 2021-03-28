package game;

import scene.FPSViewer;
import scene.Scene;

public class Game extends Scene {
    private Window window;

    public Game() {
        super();
    }

    @Override
    protected void preload() {
        // ...
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());
    }
}
