package scene;

import core.Input;
import core.MainLoop;
import core.Vector2;
import scene.physics.PhysicsProvider;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Scene extends MainLoop {
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final Viewport viewport = new Viewport();
    private final Input input = new Input();
    private Camera camera = new Camera(this);
    private PhysicsProvider physics = new PhysicsProvider(
            1.0 / MainLoop.OPTIMAL_TICKS,
            10,
            new Vector2(0, 0)
    );

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
    public Camera camera() {
        return camera;
    }

    public void addEntity(Entity entity) {
        if(!entities.contains(entity)) {
            entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Camera camera() {
        return camera;
    }

    public PhysicsProvider physics() {
        return physics;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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

        g.setTransform(new AffineTransform());
        preRender(g);

        camera.transform(g);
        AffineTransform at = g.getTransform();

        for(var e : entities) {
            g.setTransform(at);
            e.render(g);
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
