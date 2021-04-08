package game.scenes;

import game.GameTestTitle;
import scene.Camera;
import scene.Scene;
import scene.SceneRoot;
import scene.Sprite;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class PhaseBegin extends SceneRoot {
    private int phase = 1;
    private int lifePoints = 5;
    private int nplayers = 1;

    public PhaseBegin(Scene scene) {
        super(scene);
    }

    @Override
    protected void init() {
        super.init();

        var cam = scene().camera();
        cam.size().set(512, 384);
        cam.setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        cam.position().set(cam.size().width / 2.0, cam.size().height / 2.0);

        for(int i = 0; i < lifePoints; i++) {
            var sprite = addChild(new Sprite("hector"));
            sprite.setHframes(6);
            sprite.setVframes(5);
            sprite.setFrame(17);
            sprite.size().set(32, 44);
            sprite.position().set(194 + 32 * i, 252);
        }

        scene().audio().play("phase_begin");
    }

    @Override
    protected void update() {
        super.update();

        if(!scene().audio().isPlaying()) {
            ((GameTestTitle)scene()).startGame();
        }
    }

    @Override
    protected void render(Graphics2D g) {
        super.render(g);

        g.setColor(new Color(220, 242, 234));
        g.setStroke(new BasicStroke(3f));
        g.draw(new RoundRectangle2D.Double(168, 105, 184, 64, 2, 2));

        g.setFont(scene().resources().getFont("pixel").deriveFont(Font.PLAIN, 20f));
        g.drawString("PHASE " + String.format("%02d", phase), 198f, 144f);

        g.drawString(String.format("%01d", nplayers) + " PLAYER", 198f, 228f);
    }
}
