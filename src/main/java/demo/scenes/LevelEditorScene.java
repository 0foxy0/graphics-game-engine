package demo.scenes;

import engine.Scene;
import engine.Window;
import engine.listeners.KeyListener;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class LevelEditorScene extends Scene {
    private boolean sent = false;

    @Override
    public void start() {

    }

    @Override
    public void update(double deltaTime) {
        if (!sent) {
            System.out.println("Now in LevelEditorScene");
            sent = true;
        }

        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }
    }
}
