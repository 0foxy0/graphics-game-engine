package engine.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    public static double timeStarted = glfwGetTime();

    public static double getTimeInSeconds() {
        return glfwGetTime() - timeStarted;
    }
}
