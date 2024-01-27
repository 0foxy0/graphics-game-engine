package de.foxy.demo.scenes;

import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.listeners.KeyListener;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class LevelEditorScene extends Scene {
    @Override
    public void start() {
        System.out.println("Now in LevelEditorScene");
    }

    @Override
    public void update(double deltaTime) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }
    }

    @Override
    public void imGui() {
        ImGui.begin("Elements");
        ImGui.text("Work in progress");
        ImGui.end();
    }

    @Override
    public void end() {}
}
