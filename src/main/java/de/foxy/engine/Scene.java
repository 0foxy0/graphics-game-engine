package de.foxy.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.foxy.engine.components.Component;
import de.foxy.engine.renderer.Renderer;
import de.foxy.engine.utils.typeAdapter.ComponentTypeAdapter;
import de.foxy.engine.utils.typeAdapter.GameObjectTypeAdapter;
import org.joml.Vector2f;

import java.util.ArrayList;

public abstract class Scene {
    protected boolean isChangingScene = false;
    protected Camera camera = new Camera(new Vector2f());
    private boolean isRunning = false;
    protected ArrayList<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Component.class, new ComponentTypeAdapter()).registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter()).create();

    public abstract void start();

    public void runs() {
        if (isRunning) {
            return;
        }

        for (GameObject go : gameObjects) {
            go.start();
            renderer.mayAddSpriteRenderer(go);
        }
        isRunning = true;
    }

    public abstract void update(double deltaTime);

    public void imGui(double deltaTime) {}

    public abstract void end();

    public void addGameObjectToScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.add(gameObject);
            return;
        }

        gameObjects.add(gameObject);
        gameObject.start();
        renderer.mayAddSpriteRenderer(gameObject);
    }

    //fixme: correct game object gets removed but the rendering is false the deleted object gets shown and another object with the same sprite does not get rendered
    public void removeGameObjectFromScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.removeIf(go -> go.equals(gameObject));
            return;
        }

        gameObjects.removeIf(go -> go.equals(gameObject));
        renderer.mayRemoveSpriteRenderer(gameObject);
    }

    public void updateGameObjects(double deltaTime) {
        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
    }

    public void render() {
        renderer.render();
    }

    public boolean getIsChangingScene() {
        return isChangingScene;
    }

    public Camera getCamera() {
        return camera;
    }

    public GameObject getGameObjectByName(String name) {
        return gameObjects.stream().filter(go -> go.getName().equals(name)).findFirst().orElse(null);
    }

    public GameObject getGameObjectByUId(int uid) {
        return gameObjects.stream().filter(go -> go.getUid() == uid).findFirst().orElse(null);
    }

    public Gson getGson() {
        return gson;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
