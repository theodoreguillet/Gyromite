package gyromite.game.scenes;

import gyromite.game.Game;
import gyromite.scene.Camera;
import gyromite.scene.Scene;
import gyromite.scene.SceneRoot;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GameOver extends SceneRoot {
    public GameOver(Scene scene) {
        super(scene);
    }

    @Override
    protected void init() {
        super.init();

        var cam = scene().camera();
        cam.size().set(512, 384);
        cam.setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        cam.position().set(cam.size().width / 2.0, cam.size().height / 2.0);

        scene().audio().play("game_over");
    }

    @Override
    protected void update() {
        super.update();

        if(!scene().audio().isPlaying()) {
            ((Game)scene()).showMenu();
        }
    }

    @Override
    protected void render(Graphics2D g) {
        super.render(g);

        g.setColor(new Color(220, 242, 234));
        g.setStroke(new BasicStroke(3f));
        g.draw(new RoundRectangle2D.Double(168, 105, 184, 64, 2, 2));

        g.setFont(scene().resources().getFont("pixel").deriveFont(Font.PLAIN, 20f));
        g.drawString("GAME OVER ", 190f, 144f);
    }
}
