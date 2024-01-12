package engine.utils;

public class Time {
    public static long timeStarted = System.nanoTime();

    public static double getTimeInSeconds() {
        return (System.nanoTime() - timeStarted) * 1e-9;
    }
}
