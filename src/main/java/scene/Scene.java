package scene;

import core.Input;
import core.MainLoop;
import scene.physics.PhysicsProvider;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Scene extends MainLoop {
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final Viewport viewport = new Viewport();
    private final Input input = new Input();
    private final PhysicsProvider physics = new PhysicsProvider(MainLoop.DT, 10);
    private Camera camera = new Camera(this);
    private boolean antialiasing = true;
    private boolean renderPhysics = false;

    public Scene() {
        super();
        viewport.addEventListener(input);
    }

    public void addEntity(Entity entity) {
        if(!entities.contains(entity)) {
            entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Viewport viewport() {
        return viewport;
    }
    public Input input() {
        return input;
    }
    public PhysicsProvider physics() {
        return physics;
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
        for(var e : entities) {
            e.update();
        }
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
        preRender(g);

        camera.transform(g);
        AffineTransform at = g.getTransform();

        for(var e : entities) {
            g.setTransform(at);
            e.render(g);
        }

        if(renderPhysics)  {
            g.setTransform(at);
            physics.render(g);
        }

        g.setTransform(new AffineTransform());
        postRender(g);

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
