import game.Game;
import game.SceneTestGame;
import game.SceneTestInput;
import scene.Scene;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World !!");
        Scene game = new SceneTestInput();
        game.start();
    }
}
