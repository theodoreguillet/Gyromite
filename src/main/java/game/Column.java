package game;

import core.MainLoop;
import core.MathUtils;
import core.Size2;
import scene.Node;
import scene.Sprite;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.PolygonShape;

public class Column extends Node {
    public enum Type {
        BLUE,
        RED
    }
    private static final double MOVE_SPEED = 400.0;
    private final TiledMap tiledmap;
    private final Size2 size;
    private final Type type;
    private boolean move = false;
    private boolean moveTop = true;
    private boolean movePaused = false;
    private double delay = 0.0;
    private double maxY, minY;

    public Column(TiledMap tiledmap, double x, double y, double width, double height, Type type) {
        super();
        this.tiledmap = tiledmap;
        size = new Size2(width, height);
        this.type = type;
        setPosition(x, y);
        maxY = minY = y;
    }

    public boolean isMoving() {
        return move;
    }

    public void toggle() {
        moveTop = !moveTop;
        movePaused = false;
        move = true;
        delay = 0.0;
    }

    @Override
    public void init() {
        double tw = tiledmap.tilemap().tilewidth;
        double th = tiledmap.tilemap().tileheight;
        int n = (int)Math.round(size.height / th);
        double height = (double)n * th;

        for(int i = 0; i < n; i++) {
            var sprite = addChild(new Sprite("tileset"));
            sprite.setHframes(6);
            sprite.setVframes(5);
            sprite.size().set(tw, th);
            sprite.position().x = 0;
            sprite.position().y = (double)i * th - height / 2.0 + th / 2.0;
            if(i == 0) {
                sprite.setFrame(type == Type.BLUE ? 6 : 9);
            } else if(i == n - 1) {
                sprite.setFrame(type == Type.BLUE ? 8 : 11);
            } else {
                sprite.setFrame(type == Type.BLUE ? 7 : 10);
            }
        }

        size.set(tw, height);
        position().x += tw / 2.0;
        position().y += height / 2.0;

        minY = position().y;
        maxY = position().y + height - th;

        setBody(new PolygonShape(tw / 2.0, height / 2.0 - 2.0), Body.Mode.STATIC);
        body().restitution = 0.0;
    }

    @Override
    public void update() {
        if(!move) {
            return;
        }
        double movingDelay = (double)tiledmap.tilemap().tileheight / MOVE_SPEED / 2.0;
        double staticDelay = 0.2;
        if((!movePaused && delay >= movingDelay) || delay > staticDelay) {
            movePaused = !movePaused;
            if(movePaused) {
                body().velocity.y = 0.0;
            } else {
                body().velocity.y = moveTop ? -MOVE_SPEED : MOVE_SPEED;
            }
            delay = 0.0;
        }
        delay += MainLoop.DT;

        if(!moveTop && position().y >= maxY - MathUtils.EPSILON) {
            position().y = maxY;
            move = false;
        } else if(moveTop && position().y <= minY + MathUtils.EPSILON) {
            position().y = minY;
            move = false;
        }
        if(!move) {
            body().velocity.y = 0.0;
        }
    }
}
