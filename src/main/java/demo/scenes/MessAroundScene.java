package demo.scenes;

import engine.GameObject;
import engine.Scene;
import engine.Transform;
import engine.components.SpriteRenderer;
import engine.renderer.Renderer;
import engine.renderer.Shader;
import engine.utils.ShaderPreset;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MessAroundScene extends Scene {
    @Override
    public void start() {
        renderer = new Renderer(new Shader(ShaderPreset.TWOD));

        int xOffset = 10, yOffset = 10;
        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100f;
        float sizeY = totalHeight / 100f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject gameObject = new GameObject(x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                addGameObjectToScene(gameObject);
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }

        renderer.render();
    }
}
