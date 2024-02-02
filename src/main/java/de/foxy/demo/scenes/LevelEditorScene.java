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
import de.foxy.engine.utils.geometry.Line2D;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class LevelEditorScene extends Scene {
    private final int GRID_SIZE = 32;
    private final DebugDraw debugDraw = new DebugDraw(1500, 1.3f);
    private final String elementsSpriteSheetTextureFilePath = "src/main/java/de/foxy/demo/assets/elements_spritesheet.png";
    private final String saveFilePath = "level.json";
    private GameObject holdingElement = null;

    @Override
    public void start() {
        Window.getImGuiLayer().setIniFilename("imgui.ini");

        Texture elementsSpriteSheetTexture = AssetCollector.getTexture(elementsSpriteSheetTextureFilePath, true);
        SpriteSheet elementsSpriteSheet = new SpriteSheet(elementsSpriteSheetTexture, 16, 16, 81, 0);
        AssetCollector.addSpriteSheet(elementsSpriteSheet);

        try {
            String json = new String(Files.readAllBytes(Paths.get(saveFilePath)));
            GameObject[] deserializedGOs = getGson().fromJson(json, GameObject[].class);

            for (GameObject go : deserializedGOs) {
                addGameObjectToScene(go);
            }
        } catch (IOException ignored) {}
    }

    @Override
    public void update(double deltaTime) {
        drawGrid();

        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }

        if (holdingElement != null) {
            int nextX = (int) (MouseListener.getOrthoX() / GRID_SIZE) * GRID_SIZE;
            int nextY = (int) (MouseListener.getOrthoY() / GRID_SIZE) * GRID_SIZE;

            holdingElement.transform.position.x = nextX;
            holdingElement.transform.position.y = nextY;

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                holdingElement.setName("Element-" + UUID.randomUUID());
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
                GameObject gameObject = Prefab.createSpriteObject("HOLDING", sprite, new Vector3f(MouseListener.getOrthoX(), MouseListener.getOrthoY(), 0), new Vector3f(GRID_SIZE, GRID_SIZE, 0));
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
        try {
            FileWriter writer = new FileWriter(saveFilePath);
            writer.write(getGson().toJson(gameObjects.stream().filter(go -> !Objects.equals(go.getName(), "HOLDING")).toArray()));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawGrid() {
        Vector3f projectionSize = camera.getProjectionSize();

        int firstX = ((int) (camera.position.x / GRID_SIZE) - 1) * GRID_SIZE;
        int firstY = ((int) (camera.position.y / GRID_SIZE) - 1) * GRID_SIZE;

        int numOfVerticalLines = (int) (projectionSize.x / GRID_SIZE) + 2;
        int numOfHorizontalLines = (int) (projectionSize.y / GRID_SIZE) + 2;

        int width = (int) projectionSize.x + GRID_SIZE * 2;
        int height = (int) projectionSize.y + GRID_SIZE * 2;

        int maxLines = Math.max(numOfVerticalLines, numOfHorizontalLines);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + GRID_SIZE * i;
            int y = firstY + GRID_SIZE * i;

            if (i < numOfVerticalLines) {
                debugDraw.addLine2D(new Line2D(new Vector2f(x, firstY), new Vector2f(x, y + height), Integer.MAX_VALUE));
            }
            if (i < numOfHorizontalLines) {
                debugDraw.addLine2D(new Line2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), Integer.MAX_VALUE));
            }
        }
    }
}
