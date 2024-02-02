package de.foxy.demo.scenes;

import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.listeners.KeyListener;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class LevelScene extends Scene {
    @Override
    public void start() {}

    @Override
    public void update(double deltaTime) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelEditorScene());
        }
    }

    @Override
    public void end() {}
}
