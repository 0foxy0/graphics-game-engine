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
        int spriteRendererZIndex = (int) spriteRenderer.gameObject.transform.position.z;

        for (RenderBatch batch : batches) {
            if (!canAddSpriteRendererToBatch(batch, spriteRendererZIndex, spriteRenderer.getTexture())) {
                continue;
            }
            batch.addSpriteRenderer(spriteRenderer);
            return;
        }

        RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, shader, spriteRendererZIndex);
        newBatch.start();
        batches.add(newBatch);
        newBatch.addSpriteRenderer(spriteRenderer);
        Collections.sort(batches);
    }

    private boolean canAddSpriteRendererToBatch(RenderBatch batch, int spriteRendererZIndex, Texture spriteTexture) {
        return batch.hasSpriteRenderersRoom() && batch.getZIndex() == spriteRendererZIndex &&
                (spriteTexture == null || batch.hasTexture(spriteTexture) || batch.hasTextureRoom());
    }

    public void mayRemoveSpriteRenderer(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        if (spriteRenderer != null) {
            removeSpriteRenderer(spriteRenderer);
        }
    }

    private void removeSpriteRenderer(SpriteRenderer spriteRenderer) {
        int spriteRendererZIndex = (int) spriteRenderer.gameObject.transform.position.z;

        for (RenderBatch batch : batches) {
            if (batch.getZIndex() != spriteRendererZIndex) {
                continue;
            }

            batch.removeSpriteRenderer(spriteRenderer);
            if (batch.getNumOfSpriteRenderers() < 1) {
                batches.remove(batch);
            }
            return;
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
