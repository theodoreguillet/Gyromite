package gyromite.game.scenes;

import gyromite.game.Game;
import gyromite.scene.Camera;
import gyromite.scene.Scene;
import gyromite.scene.SceneRoot;
import gyromite.scene.Sprite;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PhaseBegin extends SceneRoot {
    private final int phase;
    private final int playerLife;
    private final int nplayers;

    public PhaseBegin(Scene scene, int phase, int playerLife, int nplayers) {
        super(scene);
        this.phase = phase;
        this.playerLife = playerLife;
        this.nplayers = nplayers;
    }

    @Override
    protected void init() {
        super.init();

        var cam = scene().camera();
        cam.size().set(512, 384);
        cam.setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        cam.position().set(cam.size().width / 2.0, cam.size().height / 2.0);

        for(int i = 0; i < playerLife; i++) {
            var sprite = addChild(new Sprite("player"));
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
            ((Game)scene()).startPhase();
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
