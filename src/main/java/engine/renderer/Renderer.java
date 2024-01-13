package engine.renderer;

import engine.GameObject;
import engine.components.SpriteRenderer;
import engine.utils.AssetCollector;
import engine.utils.ShaderPreset;

import java.util.ArrayList;

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

    public void addSpriteRenderer(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        if (spriteRenderer != null) {
            addSpriteRenderer(spriteRenderer);
        }
    }

    private void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        boolean added = false;

        for (RenderBatch batch : batches) {
            boolean cantAddSpriteRenderer = !batch.hasSpriteRenderersRoom() || (spriteRenderer.getTexture() != null && !batch.hasTextureRoom() && !batch.hasTexture(spriteRenderer.getTexture()));
            if (cantAddSpriteRenderer) {
                continue;
            }

            batch.addSpriteRenderer(spriteRenderer);
            added = true;
            break;
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, shader);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSpriteRenderer(spriteRenderer);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
