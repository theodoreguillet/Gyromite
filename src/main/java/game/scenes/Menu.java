package game.scenes;

import game.Game;
import scene.Camera;
import scene.Scene;
import scene.SceneRoot;
import scene.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Menu extends SceneRoot {
    enum Option {
        TEST,
        DIRECT,
        GAME_A_1P,
        GAME_A_2P,
        GAME_B
    }
    private static final double SELECTOR_POS_X = -136;
    private static final HashMap<Option, Double> SELECTOR_POS_Y = new HashMap<>();
    static {
        SELECTOR_POS_Y.put(Option.TEST, -89.5);
        SELECTOR_POS_Y.put(Option.DIRECT, SELECTOR_POS_Y.get(Option.TEST) + 48);
        SELECTOR_POS_Y.put(Option.GAME_A_1P, SELECTOR_POS_Y.get(Option.DIRECT) + 48);
        SELECTOR_POS_Y.put(Option.GAME_A_2P, SELECTOR_POS_Y.get(Option.GAME_A_1P) + 32);
        SELECTOR_POS_Y.put(Option.GAME_B, SELECTOR_POS_Y.get(Option.GAME_A_2P) + 48);
    }
    private Sprite selector;
    private Option selectedOption = Option.TEST;
    private int phase = 1;

    public Menu(Scene scene) {
        super(scene);
    }

    @Override
    protected void init() {
        super.init();

        var title = addChild(new Sprite("menu"));
        title.size().set(512, 419);
        scene().camera().size().set(512, 419);
        scene().camera().setStretchMode(Camera.StretchMode.KEEP_ASPECT);

        selector = addChild(new Sprite("smick"));
        selector.setHframes(4);
        selector.setVframes(5);
        selector.setFrame(15);
        selector.size().set(32, 32);

        scene().audio().play("select_mode");

        scene().input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ((Game)scene()).showPhaseBegin();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if(selectedOption.ordinal() > 0) {
                        selectedOption = Option.values()[selectedOption.ordinal() - 1];
                        scene().audio().play("bip");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if(selectedOption.ordinal() < Option.values().length - 1) {
                        selectedOption = Option.values()[selectedOption.ordinal() + 1];
                        scene().audio().play("bip");
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
    }

    @Override
    protected void update() {
        super.update();

        selector.position().set(SELECTOR_POS_X, SELECTOR_POS_Y.get(selectedOption));
    }

    @Override
    protected void render(Graphics2D g) {
        super.render(g);

        g.setFont(scene().resources().getFont("pixel").deriveFont(Font.PLAIN, 20f));
        g.setColor(new Color(244, 128, 175));
        g.drawString("PHASE", -64f, -128.5f);
        g.setColor(new Color(122, 150, 200));
        g.drawString(String.format("%02d" , phase), 32f, -128.5f);
    }
}
