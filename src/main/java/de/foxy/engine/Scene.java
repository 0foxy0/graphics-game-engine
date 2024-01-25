package de.foxy.engine;

import de.foxy.engine.renderer.Renderer;
import org.joml.Vector2f;

import java.util.ArrayList;

public abstract class Scene {
    protected boolean isChangingScene = false;
    protected Camera camera = new Camera(new Vector2f());
    private boolean isRunning = false;
    protected ArrayList<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();

    public abstract void start();

    public void runs() {
        if (isRunning) {
            return;
        }

        for (GameObject go : gameObjects) {
            go.start();
            renderer.addSpriteRenderer(go);
        }
        isRunning = true;
    }

    public abstract void update(double deltaTime);

    public void addGameObjectToScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.add(gameObject);
            return;
        }

        gameObjects.add(gameObject);
        gameObject.start();
        renderer.addSpriteRenderer(gameObject);
    }

    public boolean getIsChangingScene() {
        return isChangingScene;
    }

    public Camera getCamera() {
        return camera;
    }
}
