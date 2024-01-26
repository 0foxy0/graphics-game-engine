package de.foxy.demo.scenes;

import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.listeners.KeyListener;

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

    @Override
    public void end() {}
}
