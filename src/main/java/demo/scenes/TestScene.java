package demo.scenes;

import engine.GameObject;
import engine.Scene;
import engine.Transform;
import engine.components.SpriteRenderer;
import engine.components.SpriteSheet;
import engine.renderer.Texture;
import engine.utils.AssetCollector;
import org.joml.Vector2f;

public class TestScene extends Scene {
    @Override
    public void start() {
        Texture spriteSheetTexture = AssetCollector.getTexture("src/main/java/demo/assets/spritesheet.png", true);
        SpriteSheet spriteSheet = new SpriteSheet(spriteSheetTexture, 16, 16, 15, 0);

        GameObject gameObject = new GameObject("logo", new Transform(new Vector2f(), new Vector2f(200, 200)));
        gameObject.addComponent(new SpriteRenderer(spriteSheet.getSprite(0)));
        addGameObjectToScene(gameObject);

        /*int xOffset = 10, yOffset = 10;
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
        }*/
    }

    @Override
    public void update(double deltaTime) {
        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }

        renderer.render();
    }
}