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
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {
    // Properties
    private final int GRID_SIZE = 32;
    private final DebugDraw debugDraw = new DebugDraw(1500, 1.3f);
    private boolean cursorIsInViewport = false;

    // File paths
    private final String elementsSpriteSheetTextureFilePath = "src/main/java/de/foxy/demo/assets/elements_spritesheet.png";
    private final String saveFilePath = "level.json";

    // Element Selection
    private GameObject holdingElement = null;

    // Camera Movement
    private Vector3f clickOrigin;
    private float dragDebounce = 0.032f;
    private float lerpTime = 0;
    private final float dragSensitivity = 30;
    private final float scrollSensitivity = 0.1f;
    private boolean moveCamBack = false;

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
        camera.adjustProjection();

        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_TAB)) {
            Window.changeScene(new LevelScene());
        }

        if (holdingElement != null && cursorIsInViewport) {
            int nextX = (int) (MouseListener.getOrthoX() / GRID_SIZE) * GRID_SIZE;
            int nextY = (int) (MouseListener.getOrthoY() / GRID_SIZE) * GRID_SIZE;

            holdingElement.transform.position.x = nextX;
            holdingElement.transform.position.y = nextY;

            if (KeyListener.isKeyDown(GLFW_KEY_ESCAPE)) {
                holdingElement.destroy();
                holdingElement = null;
            }

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                holdingElement.setName("Element-" + UUID.randomUUID());
                holdingElement = null;
            }
        } else if (holdingElement == null && cursorIsInViewport) {
            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
                int x = (int) MouseListener.getViewportX();
                int y = (int) MouseListener.getViewportY();
                holdingElement = getGameObjectByUId(Window.getPickingTexture().readPixel(x, y));
            }
        }

        if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            clickOrigin = new Vector3f(MouseListener.getOrthoX(), MouseListener.getOrthoY(), 0);
            dragDebounce -= (float) deltaTime;
        } else if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector3f mousePosition = new Vector3f(MouseListener.getOrthoX(), MouseListener.getOrthoY(), 0);
            Vector3f delta = new Vector3f(mousePosition).sub(clickOrigin);

            camera.position.sub(delta.mul((float) deltaTime).mul(dragSensitivity));
            clickOrigin.lerp(mousePosition, (float) deltaTime);
        }

        if (dragDebounce <= 0 && !MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / camera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            camera.addZoom(addValue);
        }

        if (KeyListener.isKeyDown(GLFW_KEY_LEFT_ALT)) {
            moveCamBack = true;
        }
        if (moveCamBack) {
            Vector3f startPosition = new Vector3f(0, 0, camera.position.z);
            camera.position.lerp(startPosition, lerpTime);
            camera.setZoom(camera.getZoomLerp(1, lerpTime));
            lerpTime += (float) (0.1f * deltaTime);

            if (Math.abs(camera.position.x) <= 0.5f && Math.abs(camera.position.y) <= 5) {
                camera.position.set(startPosition);
                camera.resetZoom();
                lerpTime = 0;
                moveCamBack = false;
            }
        }

        drawGrid();
    }

    @Override
    public void imGui(double deltaTime) {
        // Setup Dockingspace
        {
            int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

            ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
            ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());

            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);

            windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

            ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);

            ImGui.popStyleVar(2);
            ImGui.dockSpace(ImGui.getID("Dockspace"));

            ImGui.end();
        }

        // Setup Level View
        {
            ImGui.begin("Level", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

            ImVec2 windowSize = getLargestSizeForViewport();
            ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
            ImGui.setCursorPos(windowPos.x, windowPos.y);

            ImVec2 topLeft = ImGui.getCursorScreenPos();
            topLeft.x -= ImGui.getScrollX();
            topLeft.y -= ImGui.getScrollY();

            float leftX = topLeft.x;
            float bottomY = topLeft.y;
            float rightX = topLeft.x + windowSize.x;
            float topY = topLeft.y + windowSize.y;

            cursorIsInViewport = MouseListener.getX() >= leftX && MouseListener.getX() <= rightX && MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;

            MouseListener.setViewportPosition(new Vector2f(topLeft.x, topLeft.y));
            MouseListener.setViewportSize(new Vector2f(windowSize.x, windowSize.y));

            int textureId = Window.getFramebuffer().getTextureId();
            ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

            ImGui.end();
        }

        // Setup Elements Window
        {
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
        }

        // FPS Display
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
            MouseListener.resetViewportToWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //note: grid is getting off as you zoom out
    private void drawGrid() {
        Vector3f projectionSize = camera.getProjectionSize();

        int firstX = ((int) (camera.position.x / GRID_SIZE) - 1) * GRID_SIZE;
        int firstY = ((int) (camera.position.y / GRID_SIZE) - 1) * GRID_SIZE;

        int numOfVerticalLines = (int) (projectionSize.x * camera.getZoom() / GRID_SIZE) + 2;
        int numOfHorizontalLines = (int) (projectionSize.y * camera.getZoom() / GRID_SIZE) + 2;

        int width = (int) (projectionSize.x * camera.getZoom()) + GRID_SIZE * 2;
        int height = (int) (projectionSize.y * camera.getZoom()) + GRID_SIZE * 2;

        int maxLines = Math.max(numOfVerticalLines, numOfHorizontalLines);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + GRID_SIZE * i;
            int y = firstY + GRID_SIZE * i;

            if (i < numOfVerticalLines) {
                debugDraw.addLine2D(new Line2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height)));
            }
            if (i < numOfHorizontalLines) {
                debugDraw.addLine2D(new Line2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y)));
            }
        }
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = ImGui.getContentRegionAvail();
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > windowSize.y) {
            aspectHeight  = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = ImGui.getContentRegionAvail();
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2f) - (aspectSize.x / 2f);
        float viewportY = (windowSize.y / 2f) - (aspectSize.y / 2f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }
}
