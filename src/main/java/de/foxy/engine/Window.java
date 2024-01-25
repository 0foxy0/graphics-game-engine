package de.foxy.engine;

import de.foxy.engine.listeners.KeyListener;
import de.foxy.engine.listeners.MouseListener;
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

    private int width, height;
    private String title;
    private Long glfwWindow;

    public Window(int width, int height, String title, Scene startScene) {
        if (window != null) {
            throw new RuntimeException("Cannot create more than one window");
        }

        this.width = width;
        this.height = height;
        this.title = title;
        currentScene = startScene;
    }

    public static Window get() {
        return window;
    }

    public static void changeScene(Scene newScene) {
        currentScene.isChangingScene = true;

        KeyListener.resetKeyInputs();
        MouseListener.resetMouseButtonInputs();

        currentScene = newScene;
        currentScene.start();
        currentScene.runs();
    }

    public void run() {
        init();
        loop();

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
        // MacOS Shader fix
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);
        // V-Sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTimeInSeconds();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }
}
