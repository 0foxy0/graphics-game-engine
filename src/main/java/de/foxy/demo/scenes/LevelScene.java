package de.foxy.demo.scenes;

import de.foxy.engine.GameObject;
import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.components.SpriteSheet;
import de.foxy.engine.listeners.KeyListener;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class LevelScene extends Scene {
    private final String marioSpriteSheetFilePath = "src/main/java/de/foxy/demo/assets/mario_spritesheet.png";
    private GameObject mario;

    @Override
    public void start() {
        Texture spriteSheetTexture = AssetCollector.getTexture(marioSpriteSheetFilePath, true);
        SpriteSheet marioSpriteSheet = new SpriteSheet(spriteSheetTexture, 16, 16, 14, 0);

        mario = new GameObject("Mario", new Transform(new Vector3f(), new Vector2f(200, 200)));
        mario.addComponent(new SpriteRenderer(marioSpriteSheet.getSprite(0)));
        addGameObjectToScene(mario);
    }

    @Override
    public void update(double deltaTime) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelEditorScene());
        }

        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
        renderer.render();
    }

    @Override
    public void end() {
        AssetCollector.removeTexture(marioSpriteSheetFilePath);
    }
}
