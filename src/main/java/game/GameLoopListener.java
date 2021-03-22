package game;

public interface GameLoopListener {
    void onPreload();
    void onInit();
    void onUpdate();
    void onUpdatePhysics();
    void onRender();
}
