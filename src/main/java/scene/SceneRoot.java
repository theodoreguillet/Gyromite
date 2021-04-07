package scene;

/**
 * The root node of the {@link Scene}
 */
public class SceneRoot extends Node {
    final Scene scene;
    public SceneRoot(Scene scene) {
        this.scene = scene;
    }

    @Override
    public Scene scene() {
        return scene;
    }

    @Override
    public void remove() {
        scene.setRoot(null);
    }
}
