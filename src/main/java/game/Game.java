package game;

import game.scenes.*;
import game.scenes.Menu;
import scene.Scene;

import java.awt.*;

public class Game extends Scene {
    private Window window;
    private Phase phase = null;
    private int nextPhase = 1;
    private int playerLife = 5;
    private int nplayers = 1;

    public Game() {
        super();
    }

    @Override
    protected void preload() {
        resources().loadFont("/fonts/pixel.ttf", "pixel");
        resources().loadFont("/fonts/pixel1.ttf", "pixel1");
        resources().loadAudio("/audios/01_Title_Screen.wav", "title");
        resources().loadAudio("/audios/02_Select_Mode.wav", "select_mode");
        resources().loadAudio("/audios/06_Phase_Begin.wav", "phase_begin");
        resources().loadAudio("/audios/09_Game_Over.wav", "game_over");
        resources().loadAudio("/audios/07_Game_A.wav", "game_a");
        resources().loadAudio("/audios/05_Time_099.wav", "time_99");
        resources().loadAudio("/audios/bip.wav", "bip");

        resources().loadImage("/img/title.png", "title");
        resources().loadImage("/img/menu.png", "menu");

        resources().loadImage("/tilemaps/tileset.png", "tileset");
        resources().loadImage("/img/player.png", "player");
        resources().loadImage("/img/smick.png", "smick");
        resources().loadImage("/img/particles.png", "particles");
        resources().loadImage("/img/bomb.png", "bomb");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        nextPhase = 2;
        showTitle();
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
    }

    public void showTitle() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new Title(this));
    }

    public void showMenu() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new Menu(this));
    }

    public void showPhaseBegin() {
        String name = String.format("phase_%02d", nextPhase);
        boolean loaded = resources().loadTilemap(
                String.format("/tilemaps/phase_%02d.json", nextPhase), name);
        if(!loaded)  {
            showMenu();
            return;
        }
        for(var layer : resources().getTilemap(name).layers) {
            if(layer.name.equals("columns_demo")) {
                layer.visible = false;
                break;
            }
        }

        camera().reset();
        input().removeAllListeners();
        setRoot(new PhaseBegin(this, nextPhase, playerLife, nplayers));
    }

    public void showGameOver() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new GameOver(this));
    }

    public void startPhase() {
        camera().reset();
        input().removeAllListeners();
        phase = new Phase(this, nextPhase);
        setRoot(phase);
    }

    public void bombRemoved() {
        if(phase != null) {
            phase.bombRemoved();
        }
    }
    public void smickDead() {
        if(phase != null) {
            phase.smickDead();
        }
    }

    public void endPhase(boolean success) {
        if(phase != null) {
            phase.end(success);
        }
    }
    public void phaseEnded(boolean success) {
        if(success) {
            nextPhase++;
            showPhaseBegin();
        } else {
            playerLife--;
            if (playerLife == 0) {
                showGameOver();
            } else {
                showPhaseBegin();
            }
        }
    }
}
