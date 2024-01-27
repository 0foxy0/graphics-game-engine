package de.foxy.demo.scenes;

import de.foxy.engine.Scene;
import de.foxy.engine.Window;
import de.foxy.engine.components.Sprite;
import de.foxy.engine.components.SpriteSheet;
import de.foxy.engine.listeners.KeyListener;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class LevelEditorScene extends Scene {
    private final String elementsSpriteSheetTextureFilePath = "src/main/java/de/foxy/demo/assets/elements_spritesheet.png";

    @Override
    public void start() {
        Window.getImGuiLayer().setIniFilename("imgui.ini");

        Texture elementsSpriteSheetTexture = AssetCollector.getTexture(elementsSpriteSheetTextureFilePath, true);
        SpriteSheet elementsSpriteSheet = new SpriteSheet(elementsSpriteSheetTexture, 16, 16, 81, 0);
        AssetCollector.addSpriteSheet(elementsSpriteSheet);
    }

    @Override
    public void update(double deltaTime) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }
    }

    @Override
    public void imGui() {
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
            if (ImGui.imageButton(textureId, spriteWidth, spriteHeight, textureCoords[0].x, textureCoords[0].y, textureCoords[2].x, textureCoords[2].y)) {
                System.out.println(i + " clicked");
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
    }

    @Override
    public void end() {}
}
