package scene;

import core.Input;
import core.MainLoop;
import core.resources.Resources;
import scene.physics.PhysicsProvider;

import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manage the game loop with a tree of nodes.
 * Contains all game elements.
 * Process the render with a {@link Camera}.
 */
public class Scene extends MainLoop {
    private final Viewport viewport = new Viewport();
    private final Input input = new Input();
    private final Resources resources = new Resources();
    private final PhysicsProvider physics = new PhysicsProvider(MainLoop.DT, 10);
    private SceneRoot root = new SceneRoot(this);
    private Camera camera = new Camera(this);
    private boolean antialiasing = true;
    private boolean renderPhysics = false;

    public Scene() {
        super();
        viewport.addEventListener(input);
    }

    /**
     * @return The view into the screen.
     */
    public Viewport viewport() {
        return viewport;
    }
    /**
     * @return The inputs manager.
     */
    public Input input() {
        return input;
    }
    /**
     * @return The resources manager.
     */
    public Resources resources() {
        return resources;
    }
    /**
     * @return The physics manager.
     */
    public PhysicsProvider physics() {
        return physics;
    }
    /**
     * @return The root of the nodes tree.
     */
    public SceneRoot root() {
        return root;
    }
    /**
     * @return The camera used to render the game in the {@link Viewport}
     */
    public Camera camera() {
        return camera;
    }
    /**
     * @return Return <code>true</code> if the antialiasing is enabled.
     *         The antialiasing is enabled by default and reduce the
     *         aliasing artifacts along the edges of rendered shapes.
     */
    public boolean isAntialiasing() {
        return antialiasing;
    }
    /**
     * @return Return <code>true</code> if the physics rendering is enabled.
     *         The physics rendering must be used for debug purposes to show
     *         bodies shapes with collisions points.
     */
    public boolean isRenderPhysics() {
        return renderPhysics;
    }

    /**
     * @param camera Set the camera used to render the game in the {@link Viewport}
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    /**
     * Set whether the antialiasing is enabled or not.
     * The antialiasing is enabled by default and reduce the
     * aliasing artifacts along the edges of rendered shapes.
     * @param antialiasing <code>true</code> to enable the antialiasing
     */
    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }
    /**
     * Set whether the physics rendering is enabled or not.
     * The physics rendering must be used for debug purposes to show
     * bodies shapes with collisions points.
     * @param renderPhysics <code>true</code> to enable the physics rendering
     */
    public void setRenderPhysics(boolean renderPhysics) {
        this.renderPhysics = renderPhysics;
    }

    @Override
    protected void processInput() {
        input.process();
    }

    @Override
    protected final void update() {
        preUpdate();
        root.update();
        camera.update();
        postUpdate();
    }

    @Override
    protected final void updatePhysics() {
        physics.step();
    }

    @Override
    protected final void render() {
        BufferStrategy bufferstrategy = viewport.getBufferStrategy();

        if (bufferstrategy == null) {
            viewport.createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D)bufferstrategy.getDrawGraphics();

        if (antialiasing)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setTransform(new AffineTransform());
        preRender((Graphics2D) g.create());

        camera.transform(g);
        AffineTransform at = g.getTransform();

        root.render((Graphics2D) g.create());

        if(renderPhysics)  {
            physics.render((Graphics2D) g.create());
        }

        g.setTransform(new AffineTransform());
        camera.drawBlackBars((Graphics2D) g.create());

        postRender((Graphics2D) g.create());

        g.dispose();
        bufferstrategy.show();
    }

    protected void preUpdate() { }
    protected void postUpdate() { }
    protected void preRender(Graphics2D g) {
        g.clearRect(0, 0, viewport().getWidth(), viewport.getHeight());
    }
    protected void postRender(Graphics2D g) { }
}
