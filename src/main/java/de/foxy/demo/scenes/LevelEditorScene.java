package de.foxy.demo.scenes;

import de.foxy.engine.GameObject;
import de.foxy.engine.Prefab;
import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.components.Sprite;
import de.foxy.engine.components.SpriteSheet;
import de.foxy.engine.listeners.KeyListener;
import de.foxy.engine.listeners.MouseListener;
import de.foxy.engine.renderer.DebugDraw;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.Line2D;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class LevelEditorScene extends Scene {
    private final String elementsSpriteSheetTextureFilePath = "src/main/java/de/foxy/demo/assets/elements_spritesheet.png";
    private GameObject holdingElement = null;
    DebugDraw debugDraw = new DebugDraw();

    @Override
    public void start() {
        Window.getImGuiLayer().setIniFilename("imgui.ini");

        Texture elementsSpriteSheetTexture = AssetCollector.getTexture(elementsSpriteSheetTextureFilePath, true);
        SpriteSheet elementsSpriteSheet = new SpriteSheet(elementsSpriteSheetTexture, 16, 16, 81, 0);
        AssetCollector.addSpriteSheet(elementsSpriteSheet);
    }

    @Override
    public void update(double deltaTime) {
        debugDraw.draw();

        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }

        if (holdingElement != null) {
            holdingElement.transform.position.x = MouseListener.getOrthoX() - holdingElement.transform.scale.x / 2;
            holdingElement.transform.position.y = MouseListener.getOrthoY() - holdingElement.transform.scale.y / 2;

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                debugDraw.addLine2D(new Line2D(new Vector2f(0, 0), new Vector2f(holdingElement.transform.position.x, holdingElement.transform.position.y)));
                holdingElement = null;
            }
        }

        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
        renderer.render();
    }

    @Override
    public void imGui(double deltaTime) {
        SpriteSheet elementsSpriteSheet = AssetCollector.getSpriteSheet(elementsSpriteSheetTextureFilePath);

        ImGui.begin("Elements");

        ImVec2 windowPos = ImGui.getWindowPos();
        ImVec2 windowSize = ImGui.getWindowSize();
        ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();
        float windowX2 = windowPos.x + windowSize.x;

        for (int i = 0; i < elementsSpriteSheet.getNumOfSprites(); i++) {
            Sprite sprite = elementsSpriteSheet.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int textureId = sprite.getTextureId();
            Vector2f[] textureCoords = sprite.getTextureCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(textureId, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
                GameObject gameObject = Prefab.generateSpriteObject("Element: " + i, sprite, new Vector3f(MouseListener.getOrthoX(), MouseListener.getOrthoY(), 0), new Vector3f(spriteWidth, spriteHeight, 0));
                holdingElement = gameObject;
                addGameObjectToScene(gameObject);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = ImGui.getItemRectMax();
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < elementsSpriteSheet.getNumOfSprites() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();

        ImGui.begin("FPS");
        ImGui.text(String.valueOf((int) (1.0 / deltaTime)));
        ImGui.end();
    }

    @Override
    public void end() {
    }
}
