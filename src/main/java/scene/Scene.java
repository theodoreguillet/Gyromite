package scene;

import core.Input;
import core.MainLoop;
import core.resources.Resources;
import scene.physics.PhysicsProvider;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;

public class Scene extends MainLoop {
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final Viewport viewport = new Viewport();
    private final Input input = new Input();
    private final Resources resources = new Resources();
    private final PhysicsProvider physics = new PhysicsProvider(MainLoop.DT, 10);
    private Camera camera = new Camera(this);
    private boolean antialiasing = true;
    private boolean renderPhysics = false;

    private boolean updatingEntities = false;
    private boolean removeEntity = false;

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
        updateEntities();
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

    void addEntity(Entity entity) {
        if(!entities.contains(entity)) {
            entities.add(entity);
        }
    }
    void removeEntity(Entity entity) {
        if(updatingEntities) {
            removeEntity = true;
        } else {
            entities.remove(entity);
        }
    }

    /**
     * Update entities and allow entity remove during update
     */
    private void updateEntities() {
        updatingEntities = true;
        Iterator<Entity> it = entities.iterator();
        while(it.hasNext()) {
            Entity e = it.next();
            removeEntity = false;
            e.update();
            if(removeEntity) {
                it.remove();
            }
        }
        updatingEntities = false;
    }
}
