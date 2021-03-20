package core;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainLoop {
    private final Thread thread = new Thread(this::run);
    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean paused = new AtomicBoolean();

    private static final long NANOSECOND        = 1000000000;
    private static final double OPTIMAL_TICKS   = 50.0;
    private static final double OPTIMAL_TIME    = NANOSECOND / OPTIMAL_TICKS;

    private long lastLoopTime = System.nanoTime();
    private double deltaTime = 0.0;

    public synchronized void start() {
        running.set(true);
        thread.start();
    }

    public synchronized void destroy() { // Must be called from the main thread
        if(Thread.currentThread().getId() == thread.getId()) {
            throw new RuntimeException("destroy cannot be called from the game thread");
        }
        running.set(false);
        while (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public boolean isPaused() {
        return this.paused.get();
    }

    private void run() {
        preload();
        init();
        while(running.get()) {
            // get delta time
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastLoopTime) / OPTIMAL_TIME;
            lastLoopTime = currentTime;

            // update the game
            while (deltaTime >= 1) {
                if(!this.paused.get()) {
                    update();
                }
                deltaTime--;
            }

            // render the game
            render();
        }
    }

    protected void preload() { }
    protected void init() { }
    protected void update() { }
    protected void updatePhysics() { }
    protected void render() { }
}
