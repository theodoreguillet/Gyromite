package game;

import core.Vector2;
import core.resources.tilemap.TileMap;
import scene.FPSViewer;
import scene.Scene;
import scene.Sprite;
import scene.map.TileMapBuilder;

import java.awt.*;

public class SceneTestTilemap extends Scene {
    private Window window;

    private static final double GAME_HEIGHT = 500;

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadTilemap("/tilemaps/test.json", "testmap");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = new FPSViewer(this);

        camera().setZoom(new Vector2(1, 1));

        new TileMapBuilder(this, "testmap")
                .setObjectFactory(2, 3, (builder, object, layer) -> {
                    Sprite s = new Sprite(builder.scene(), "test");
                    s.setPosition(builder.getObjectPosition(object));
                    s.size().set(object.width, object.height);
                    s.setOpacity(layer.opacity);
                    return s;
                })
                .enableCollisions(46, 40)
                .enableCollisions("cactus")
                .build();

        setRenderPhysics(true);
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(5, 5, viewport().getWidth() - 10, viewport().getHeight() - 10);
    }

    @Override
    protected void preUpdate() {
        double zoom = viewport().getHeight() / GAME_HEIGHT;
        camera().setZoom(new Vector2(zoom, zoom));
    }
}
