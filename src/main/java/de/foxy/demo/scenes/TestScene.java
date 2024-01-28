package de.foxy.demo.scenes;

import com.google.gson.Gson;
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
    private final String marioSpriteSheetFilePath = "src/main/java/de/foxy/demo/assets/mario_spritesheet.png";
    private int[] spriteIndex = {0};

    @Override
    public void start() {
        Texture spriteSheetTexture = AssetCollector.getTexture(marioSpriteSheetFilePath, true);
        SpriteSheet marioSpriteSheet = new SpriteSheet(spriteSheetTexture, 16, 16, 14, 0);
        AssetCollector.addSpriteSheet(marioSpriteSheet);

        mario = new GameObject("Mario", new Transform(new Vector3f(), new Vector2f(200, 200)));
        mario.addComponent(new SpriteRenderer(marioSpriteSheet.getSprite(spriteIndex[0])));
        addGameObjectToScene(mario);

        Gson gson = getGson();
        String serialized = gson.toJson(mario);
        GameObject deserialized = gson.fromJson(serialized, GameObject.class);
    }

    @Override
    public void update(double deltaTime) {
        SpriteSheet marioSpriteSheet = AssetCollector.getSpriteSheet(marioSpriteSheetFilePath);
        mario.getComponent(SpriteRenderer.class).setSprite(marioSpriteSheet.getSprite(spriteIndex[0]));

        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
        renderer.render();
    }

    @Override
    public void imGui() {
        SpriteSheet marioSpriteSheet = AssetCollector.getSpriteSheet(marioSpriteSheetFilePath);

        ImGui.begin("Sprite Picker");

        ImGui.pushItemWidth(ImGui.getFontSize() * 10);
        ImGui.sliderInt(" ", spriteIndex, 0, marioSpriteSheet.getNumOfSprites() - 1);
        spriteIndex[0] = Math.abs(spriteIndex[0]) % marioSpriteSheet.getNumOfSprites();

        ImGui.end();
    }

    @Override
    public void end() {
        AssetCollector.removeTexture(marioSpriteSheetFilePath);
        AssetCollector.removeSpriteSheet(marioSpriteSheetFilePath);
    }
}
