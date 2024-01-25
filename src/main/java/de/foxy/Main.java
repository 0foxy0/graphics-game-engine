package de.foxy;

import de.foxy.demo.scenes.TestScene;
import de.foxy.engine.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "Engine Demo", new TestScene());
        window.run();
    }
}
