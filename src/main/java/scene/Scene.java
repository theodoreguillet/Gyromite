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

    public Viewport viewport() {
        return viewport;
    }
    public Input input() {
        return input;
    }
    public Resources resources() {
        return resources;
    }
    public PhysicsProvider physics() {
        return physics;
    }
    public SceneRoot root() {
        return root;
    }
    public Camera camera() {
        return camera;
    }
    public boolean isAntialiasing() {
        return antialiasing;
    }
    public boolean isRenderPhysics() {
        return renderPhysics;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }
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
