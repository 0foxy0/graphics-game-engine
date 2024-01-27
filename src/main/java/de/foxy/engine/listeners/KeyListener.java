package de.foxy.engine.listeners;

import imgui.ImGui;
import imgui.ImGuiIO;

import java.util.HashMap;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;

    private HashMap<Integer, Boolean> keyPressed = new HashMap<>();

    private KeyListener() {}

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void initCallbacks(long glfwWindow) {
        ImGuiIO io = ImGui.getIO();

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            keyCallback(w, key, scancode, action, mods);

            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed.put(key, true);
        } else if (action == GLFW_RELEASE) {
            get().keyPressed.remove(key);
        }
    }

    public static boolean isKeyDown(int key) {
        return Optional.ofNullable(get().keyPressed.get(key)).orElse(false);
    }

    public static void resetKeyInputs() {
        get().keyPressed.clear();
    }
}
