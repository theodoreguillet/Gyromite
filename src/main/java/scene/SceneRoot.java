package scene;

public class SceneRoot extends Node {
    final Scene scene;
    public SceneRoot(Scene scene) {
        this.scene = scene;
    }

    @Override
    public Scene scene() {
        return scene;
    }
}
