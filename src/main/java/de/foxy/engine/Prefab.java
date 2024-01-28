package de.foxy.engine;

import de.foxy.engine.components.Sprite;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.utils.Transform;
import org.joml.Vector3f;

public class Prefab {
    public static GameObject generateSpriteObject(String name, Sprite sprite, Vector3f position, Vector3f scale) {
        GameObject gameObject = new GameObject(name, new Transform(position, scale));
        gameObject.addComponent(new SpriteRenderer(sprite));
        return gameObject;
    }
}
