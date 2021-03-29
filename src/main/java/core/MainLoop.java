package core;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainLoop {
    private final Thread thread = new Thread(this::run);
    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean paused = new AtomicBoolean();

    public static final long NANOSECOND         = 1000000000;
    public static final double OPTIMAL_TICKS    = 50.0;
    public static final double OPTIMAL_FPS      = 60.0;
    public static final double NANOS_PER_TICK   = NANOSECOND / OPTIMAL_TICKS;
    public static final double NANOS_PER_RENDER = NANOSECOND / OPTIMAL_FPS;
    public static final double DT               = 1.0 / OPTIMAL_TICKS;

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
        long nextTick = System.nanoTime();
        long nextSecond = nextTick;
        long nextRender = nextTick;

        preload();
        init();

        while (running.get()) {
            final long now = System.nanoTime();

            // update the game
            if (now - nextTick >= 0 && !paused.get()) {
                processInput();
                do {
                    updatePhysics();
                    update();

                    nextTick += NANOS_PER_TICK;
                } while (now - nextTick >= 0);
            }

            if (now - nextRender >= 0) {
                // render the game
                render();
                do {
                    // skip render lag
                    nextRender += NANOS_PER_RENDER;
                } while (now - nextRender >= 0);
            }

            // delay to the next loop
            final long workTime = System.nanoTime();
            final long minDelay = Math.min(nextSecond - workTime,
                    Math.min(nextTick - workTime, nextRender - workTime));

            if (minDelay > 0) {
                long milliDelay = (minDelay + 1_000_000) / 1_000_000L;
                try {
                    Thread.sleep(milliDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    protected void preload() { }
    protected void init() { }
    protected void processInput() { }
    protected void update() { }
    protected void updatePhysics() { }
    protected void render() { }
}
