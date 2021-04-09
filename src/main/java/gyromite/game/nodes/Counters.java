package gyromite.game.nodes;

import gyromite.core.MainLoop;
import gyromite.core.Size2;
import gyromite.game.Game;
import gyromite.scene.Node;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Counters extends Node {
    private static final double TIME_SCALE = 2.5;

    private int score = 0;
    private double timeLeft = 999;

    public void incrementScore(int reward) {
        score += reward;
    }

    public int timeLeft() {
        return (int)Math.ceil(timeLeft);
    }

    @Override
    public void update() {
        super.update();

        var camera = scene().camera();
        position().x = camera.position().x - camera.size().width / 2.0;
        position().y = camera.position().y - camera.size().height / 2.0 + 26.0;
        timeLeft = Math.max(0.0, timeLeft - MainLoop.DT * TIME_SCALE);

        if(timeLeft == 0) {
            ((Game)scene()).endPhase(false);
        }
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        Size2 size = scene().camera().size();

        g.scale(size.width / 256.0, size.height / 224.0);

        double w1 = 32.0;
        double w2 = 80.0;

        g.setStroke(new BasicStroke(1));

        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 0, w1, 0));
        g.setColor(Color.ORANGE);
        g.draw(new Rectangle2D.Double(-2, 4, w1 + 2, 6));
        g.draw(new Line2D.Double(0, 7, w1, 7));
        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 14, w1, 14));

        g.translate(w1, 0);
        g.setColor(Color.BLUE);
        g.draw(new RoundRectangle2D.Double(5, 0, 70, 14, 1, 1));
        g.setColor(new Color(168, 228, 252));
        g.setFont(scene().resources().getFont("pixel1").deriveFont(Font.PLAIN, 9.5f));
        g.drawString("1p-", 6, 11);
        g.setColor(Color.GREEN);
        g.setFont(scene().resources().getFont("pixel").deriveFont(Font.PLAIN, 11f));
        g.drawString(String.format("%06d" , score), 22, 11);
        g.translate(w2, 0);

        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 0, w1, 0));
        g.setColor(Color.ORANGE);
        g.draw(new Rectangle2D.Double(0, 4, w1, 6));
        g.draw(new Line2D.Double(0, 7, w1, 7));
        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 14, w1, 14));

        g.translate(w1, 0);
        g.setColor(Color.BLUE);
        g.draw(new RoundRectangle2D.Double(5, 0, 70, 14, 1, 1));
        g.setColor(Color.GREEN);
        g.setFont(scene().resources().getFont("pixel").deriveFont(Font.PLAIN, 11f));
        g.drawString("TIME " + String.format("%03d" , timeLeft()), 8, 11);
        g.translate(w2, 0);

        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 0, w1, 0));
        g.setColor(Color.ORANGE);
        g.draw(new Rectangle2D.Double(0, 4, w1 + 2, 6));
        g.draw(new Line2D.Double(0, 7, w1, 7));
        g.setColor(Color.RED);
        g.draw(new Line2D.Double(0, 14, w1, 14));
    }
}
