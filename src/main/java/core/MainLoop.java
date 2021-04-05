package core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for the game loop.
 */
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

    /**
     * Start the loop. The game thread will be started.
     */
    public synchronized void start() {
        running.set(true);
        thread.start();
    }

    /**
     * Destroy the game, stop the thread and join.
     * Must be called from the main thread.
     * @throws RuntimeException If this method is called from the game thread.
     */
    public synchronized void destroy() {
        if(Thread.currentThread().getId() == thread.getId()) {
            throw new RuntimeException("Destroy cannot be called from the game thread");
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

    /**
     * Set the game pause state.
     * If paused, the game will wait until this method is called from another thread to resume.
     * @param paused <code>true</code> to pause the game, <code>false</code> to resume if paused.
     */
    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    /**
     * @return <code>true</code> if the game is paused.
     */
    public boolean isPaused() {
        return this.paused.get();
    }

    /**
     * Run the game loop
     */
    private void run() {
        long nextTick = System.nanoTime();
        long nextSecond = nextTick;
        long nextRender = nextTick;

        preload();
        init();

        while (running.get()) {
            final long now = System.nanoTime();

            // update the game
            boolean isPaused = paused.get();
            if (now - nextTick >= 0) {
                if(!isPaused) {
                    processInput();
                }
                do {
                    if(!isPaused) {
                        updatePhysics();
                        update();
                    }

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

    /**
     * Preload method.
     * Should be used to load resources.
     */
    protected void preload() { }

    /**
     * Initialization method.
     * Should be used to initialize and create the game.
     */
    protected void init() { }
    /**
     * Input process tick.
     */
    protected void processInput() { }
    /**
     * Update tick.
     */
    protected void update() { }
    /**
     * Physics update tick.
     */
    protected void updatePhysics() { }
    /**
     * Render tick.
     */
    protected void render() { }
}
