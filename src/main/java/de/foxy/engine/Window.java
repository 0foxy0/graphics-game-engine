package de.foxy.engine;

import de.foxy.engine.listeners.KeyListener;
import de.foxy.engine.listeners.MouseListener;
import de.foxy.engine.renderer.DebugDraw;
import de.foxy.engine.utils.Time;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window window = null;
    private static Scene currentScene;

    private ImGuiLayer imGuiLayer;

    private int width, height;
    private String title;
    private Long glfwWindow;

    public Window(int width, int height, String title, Scene startScene) {
        if (window != null) {
            throw new RuntimeException("Cannot create more than one Window");
        }

        this.width = width;
        this.height = height;
        this.title = title;
        currentScene = startScene;
        window = this;
    }

    public static Window get() {
        return window;
    }

    public static void changeScene(Scene newScene) {
        currentScene.isChangingScene = true;
        currentScene.end();

        KeyListener.resetKeyInputs();
        MouseListener.resetMouseButtonInputs();

        currentScene = newScene;
        currentScene.start();
        currentScene.runs();
    }

    public void run() {
        init();
        loop();
        freeMemory();
    }

    private void freeMemory() {
        imGuiLayer.freeMemory();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        // Resizable is default true
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        // OpenGL Version
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(glfwWindow);
        // V-Sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.init();

        // Callbacks
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        MouseListener.initCallbacks(glfwWindow);
        KeyListener.initCallbacks(glfwWindow);

        currentScene.start();
        currentScene.runs();
    }

    private void loop() {
        double beginTime = Time.getTimeInSeconds();
        double deltaTime = -1.0;
        double endTime;

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();
            glClearColor(1f, 1f, 1f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0.0) {
                //System.out.println((1.0 / deltaTime) + " FPS");
                DebugDraw.draw();
                currentScene.update(deltaTime);
            }

            imGuiLayer.update(deltaTime);
            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTimeInSeconds();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.end();
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void setWidth(int width) {
        get().width = width;
    }

    public static void setHeight(int height) {
        get().height = height;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }
}
