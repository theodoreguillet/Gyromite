package scene.physics;

/**
 * Listen {@link Body} collision events.
 */
public interface BodyListener {
    void bodyEntered(Body b);
    void bodyExited(Body b);
}
