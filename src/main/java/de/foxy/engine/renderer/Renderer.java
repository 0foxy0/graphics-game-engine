package de.foxy.engine.renderer;

import de.foxy.engine.GameObject;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.ShaderPreset;

import java.util.ArrayList;
import java.util.Collections;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private ArrayList<RenderBatch> batches = new ArrayList<>();
    private Shader shader;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public Renderer() {
        this.shader = AssetCollector.getShader(ShaderPreset.DEFAULT.getAbsolutePath());
    }

    public void mayAddSpriteRenderer(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        if (spriteRenderer != null) {
            addSpriteRenderer(spriteRenderer);
        }
    }

    private void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        boolean added = false;
        int spriteRendererZIndex = (int) spriteRenderer.gameObject.transform.position.z;

        for (RenderBatch batch : batches) {
            if (batch.hasSpriteRenderersRoom() && batch.getZIndex() == spriteRendererZIndex) {
                Texture texture = spriteRenderer.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSpriteRenderer(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, shader, spriteRendererZIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSpriteRenderer(spriteRenderer);
            Collections.sort(batches);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
