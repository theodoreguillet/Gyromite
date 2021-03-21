package scene;

import core.MainLoop;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Scene extends MainLoop {
    private final Viewport viewport = new Viewport();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private Camera camera = new Camera(this);

    public Viewport viewport() {
        return viewport;
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

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    protected void processInput() {
        // ...
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
        // ...
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
    protected void preRender(Graphics2D g) { }
    protected void postRender(Graphics2D g) { }
}
