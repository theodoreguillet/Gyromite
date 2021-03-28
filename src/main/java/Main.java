import game.SceneTestGame;
import game.SceneTestPhysics;
import game.SceneTestSprite;
import game.SceneTestTilemap;
import scene.Scene;

public class Main {
    public static void main(String[] args) {
        Scene game = new SceneTestPhysics();
        game.start();
    }
}
