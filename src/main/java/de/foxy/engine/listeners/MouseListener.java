package de.foxy.engine.listeners;

import imgui.ImGui;
import imgui.ImGuiIO;

import java.util.HashMap;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;

    private double scrollX, scrollY;
    private double posX, posY, lastX, lastY;
    private HashMap<Integer, Boolean> mouseButtonPressed = new HashMap<>();
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0;
        this.scrollY = 0;
        this.posX = 0;
        this.posY = 0;
        this.lastX = 0;
        this.lastY = 0;
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    public static void initCallbacks(long glfwWindow) {
        ImGuiIO io = ImGui.getIO();

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            mouseButtonCallback(w, button, action, mods);

            boolean[] mouseDown = new boolean[5];

            for (int i = 0; i < mouseDown.length; i++) {
                mouseDown[i] = button == i && action != GLFW_RELEASE;
                // GLFW_MOUSE_BUTTON_1 - GLFW_MOUSE_BUTTON_5
            }

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }
        });
        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            mouseScrollCallback(w, xOffset, yOffset);

            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
        });
    }

    public static void mousePosCallback(long window, double posX, double posY) {
        get().lastX = get().posX;
        get().lastY = get().posY;

        get().posX = posX;
        get().posY = posY;

        get().isDragging = !get().mouseButtonPressed.isEmpty();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().mouseButtonPressed.put(button, true);
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonPressed.remove(button);
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;

        get().lastX = get().posX;
        get().lastY = get().posY;
    }

    public static float getX() {
        return (float) get().posX;
    }
    public static float getY() {
        return (float) get().posY;
    }

    public static float getDeltaX() {
        return (float) (get().lastX - get().posX);
    }
    public static float getDeltaY() {
        return (float) (get().lastY - get().posY);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }
    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean isMouseButtonDown(int button) {
        return Optional.ofNullable(get().mouseButtonPressed.get(button)).orElse(false);
    }

    public static void resetMouseButtonInputs() {
        get().mouseButtonPressed.clear();
        get().isDragging = false;
    }
}
