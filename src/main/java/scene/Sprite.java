package scene;

import java.awt.*;

public class Sprite extends Entity {
    public Sprite(Scene scene) {
        super(scene);
    }

    @Override
    public void update() {
        super.update();

        // ...
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        g.setColor(Color.RED);
        g.fillRect(-50, -50, 100, 100);
        g.setColor(Color.BLACK);
        g.fillRect(-45, -45, 90, 90);

        g.setColor(Color.BLUE);
        g.fillRect(-2, -2, 4, 4);

        // ...
    }
}
