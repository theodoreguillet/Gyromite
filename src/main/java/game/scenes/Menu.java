package game.scenes;

import game.GameTestTitle;
import scene.Camera;
import scene.Scene;
import scene.SceneRoot;
import scene.Sprite;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Menu extends SceneRoot {
    public Menu(Scene scene) {
        super(scene);
    }

    @Override
    protected void init() {
        var title = addChild(new Sprite("menu"));
        title.size().set(512, 448);
        scene().camera().size().set(512, 448);
        scene().camera().setStretchMode(Camera.StretchMode.KEEP_ASPECT);

        scene().input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n') {
                    ((GameTestTitle)scene()).showGame();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
    }
}
