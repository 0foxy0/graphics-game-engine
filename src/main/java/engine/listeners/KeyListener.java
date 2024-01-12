package engine.listeners;

import java.util.HashMap;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

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
