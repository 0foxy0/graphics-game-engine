package de.foxy.demo.scenes;

import de.foxy.engine.GameObject;
import de.foxy.engine.Scene;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.components.SpriteSheet;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class TestScene extends Scene {
    private final String marioSpriteSheetFilePath = "src/main/java/de/foxy/demo/assets/mario_spritesheet.png";
    private final String saveFilePath = "testscene.json";
    private GameObject mario;
    private int[] spriteIndex = {0};

    @Override
    public void start() {
        Texture spriteSheetTexture = AssetCollector.getTexture(marioSpriteSheetFilePath, true);
        SpriteSheet marioSpriteSheet = new SpriteSheet(spriteSheetTexture, 16, 16, 14, 0);
        AssetCollector.addSpriteSheet(marioSpriteSheet);

        try {
            String json = new String(Files.readAllBytes(Paths.get(saveFilePath)));
            GameObject[] deserializedGOs = getGson().fromJson(json, GameObject[].class);

            for (GameObject go : deserializedGOs) {
                addGameObjectToScene(go);
            }
        } catch (IOException ignored) {}

        Optional<GameObject> marioOptional = getGameObjectByName("Mario");
        if (marioOptional.isPresent()) {
            mario = marioOptional.get();
            mario.getComponent(SpriteRenderer.class).setSprite(marioSpriteSheet.getSprite(spriteIndex[0]));
        }

        if (marioOptional.isPresent()) {
            return;
        }

        mario = new GameObject("Mario", new Transform(new Vector3f(), new Vector2f(200, 200)));
        mario.addComponent(new SpriteRenderer(marioSpriteSheet.getSprite(spriteIndex[0])));
        addGameObjectToScene(mario);
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
    public void imGui(double deltaTime) {
        SpriteSheet marioSpriteSheet = AssetCollector.getSpriteSheet(marioSpriteSheetFilePath);

        ImGui.begin("Sprite Picker");

        ImGui.pushItemWidth(ImGui.getFontSize() * 10);
        ImGui.sliderInt(" ", spriteIndex, 0, marioSpriteSheet.getNumOfSprites() - 1);
        spriteIndex[0] = Math.abs(spriteIndex[0]) % marioSpriteSheet.getNumOfSprites();

        ImGui.end();
    }

    @Override
    public void end() {
        try {
            FileWriter writer = new FileWriter(saveFilePath);
            writer.write(getGson().toJson(gameObjects));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AssetCollector.removeTexture(marioSpriteSheetFilePath);
        AssetCollector.removeSpriteSheet(marioSpriteSheetFilePath);
    }
}
