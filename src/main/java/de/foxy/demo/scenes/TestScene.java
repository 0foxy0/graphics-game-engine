package de.foxy.demo.scenes;

import de.foxy.engine.GameObject;
import de.foxy.engine.Scene;
import de.foxy.engine.Transform;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.components.SpriteSheet;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestScene extends Scene {
    private GameObject mario;
    private final String spriteSheetFilePath = "src/main/java/de/foxy/demo/assets/spritesheet.png";
    private int[] spriteIndex = {0};

    @Override
    public void start() {
        Texture spriteSheetTexture = AssetCollector.getTexture(spriteSheetFilePath, true);
        SpriteSheet spriteSheet = new SpriteSheet(spriteSheetTexture, 16, 16, 14, 0);
        AssetCollector.addSpriteSheet(spriteSheet);

        mario = new GameObject("mario", new Transform(new Vector3f(), new Vector2f(200, 200)));
        mario.addComponent(new SpriteRenderer(spriteSheet.getSprite(0)));
        addGameObjectToScene(mario);

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
        SpriteSheet spriteSheet = AssetCollector.getSpriteSheet(spriteSheetFilePath);
        mario.getComponent(SpriteRenderer.class).setSprite(spriteSheet.getSprite(spriteIndex[0]));

        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }

        renderer.render();
    }

    @Override
    public void imGui() {
        SpriteSheet spriteSheet = AssetCollector.getSpriteSheet(spriteSheetFilePath);

        //ImGui.showDemoWindow();
        ImGui.begin("Sprite Picker");

        ImGui.pushItemWidth(ImGui.getFontSize() * 10);
        ImGui.sliderInt(" ", spriteIndex, 0, spriteSheet.getNumOfSprites() - 1);

        ImGui.end();
    }
}
