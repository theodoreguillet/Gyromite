package gyromite.game.nodes;

import gyromite.core.MainLoop;
import gyromite.scene.Sprite;
import gyromite.scene.physics.Body;
import gyromite.scene.physics.CircleShape;

public class Radish extends Sprite {
    private final boolean takable;
    private double eatingDelay = -1;

    public Radish(boolean takable) {
        super();
        this.takable = takable;
    }

    public boolean isEaten() {
        return eatingDelay == 0.0;
    }

    public void setGettingEaten() {
        eatingDelay = 5.0;
    }

    @Override
    protected void init() {
        super.init();

        size().set(32, 32);
        setImage("smick");
        setHframes(4);
        setVframes(5);
        setFrame(17);

        if(takable) {
            setBody(new CircleShape(7), Body.Mode.TRANSPARENT);
        }
    }

    @Override
    protected void update() {
        super.update();

        if(eatingDelay != -1) {
            eatingDelay = Math.max(0, eatingDelay - MainLoop.DT);
        }
    }
}
