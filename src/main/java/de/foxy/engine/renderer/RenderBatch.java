package de.foxy.engine.renderer;

import de.foxy.engine.Window;
import de.foxy.engine.components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Sizes
    private final int POSITION_SIZE = 2, COLOR_SIZE = 4, TEXTURE_COORDS_SIZE = 2, TEXTURE_ID_SIZE = 1, ENTITY_ID_SIZE = 1;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE + TEXTURE_COORDS_SIZE + TEXTURE_ID_SIZE + ENTITY_ID_SIZE;
    private final int VERTEX_SIZE_IN_BYTES = VERTEX_SIZE * Float.BYTES;
    // Offsets
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_SIZE * Float.BYTES;
    private final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;
    // 6 indices per quad/rectangle; 3 indices per triangle
    private final int VERTICES_PER_QUAD = 4, INDICES_PER_QUAD = 6;

    private final int TEXTURE_LIMIT = 8;

    private SpriteRenderer[] spriteRenderers;
    private int numOfSpriteRenderers = 0;
    private boolean renderersArrayHasRoom = true;
    private float[] vertices;

    private int vaoId, vboId;
    private int maxBatchSize;
    private int zIndex;
    private ArrayList<Texture> textures = new ArrayList<>();
    private int[] textureSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];

        this.vertices = new float[maxBatchSize * VERTICES_PER_QUAD * VERTEX_SIZE];
    }

    public void start() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void render() {
        boolean rebufferData = false;

        for (int i = 0; i < numOfSpriteRenderers; i++) {
            SpriteRenderer spriteRenderer = spriteRenderers[i];
            if (!spriteRenderer.hasChanged()) {
                continue;
            }

            loadVertexProperties(i);
            spriteRenderer.changeAcknowledged();
            rebufferData = true;
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Window.getCurrentScene().getRenderer().getShader();

        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numOfSpriteRenderers * INDICES_PER_QUAD, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        for (Texture texture : textures) {
            texture.unbind();
        }
        shader.detach();
    }

    public void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        int index = numOfSpriteRenderers;

        spriteRenderers[index] = spriteRenderer;
        Texture texture = spriteRenderer.getTexture();

        numOfSpriteRenderers++;

        if (texture != null) {
            if (!hasTextureRoom()) {
                throw new IllegalStateException("Cannot have more than "+TEXTURE_LIMIT+" Textures per RenderBatch");
            }

            if (!textures.contains(texture)) {
                textures.add(texture);
            }
        }

        loadVertexProperties(index);

        if (numOfSpriteRenderers >= maxBatchSize) {
            renderersArrayHasRoom = false;
        }
    }

    public void removeSpriteRenderer(SpriteRenderer spriteRenderer) {
        ArrayList<SpriteRenderer> list = new ArrayList<>(Arrays.asList(spriteRenderers));
        list.remove(spriteRenderer);

        spriteRenderers = list.toArray(new SpriteRenderer[maxBatchSize]);
        numOfSpriteRenderers--;

        Texture texture = spriteRenderer.getTexture();

        if (texture != null) {
            boolean isUsedMoreThanOnce = false;

            for (SpriteRenderer sprRnd : spriteRenderers) {
                if (sprRnd.getTexture().equals(texture)) {
                    isUsedMoreThanOnce = true;
                    break;
                }
            }

            if (!isUsedMoreThanOnce) {
                textures.remove(texture);
            }
        }

        if (!renderersArrayHasRoom) {
            renderersArrayHasRoom = true;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = spriteRenderers[index];
        Vector4f color = spriteRenderer.getColor();
        Vector2f[] textureCoords = spriteRenderer.getTextureCoords();

        int textureId = 0;

        if (spriteRenderer.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(spriteRenderer.getTexture())) {
                    textureId = i + 1;
                    break;
                }
            }
        }

        int offset = index * VERTICES_PER_QUAD * VERTEX_SIZE;

        float xAdd = 1f;
        float yAdd = 1f;

        for (int i = 0; i < VERTICES_PER_QUAD; i++) {
            switch (i) {
                case 1:
                    yAdd = 0f;
                    break;
                case 2:
                    xAdd = 0f;
                    break;
                case 3:
                    yAdd = 1f;
                    break;
            }

            // Position
            vertices[offset] = spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x);
            vertices[offset + 1] = spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y);

            // Color
            for (int j = 2; j < COLOR_SIZE + 2; j++) {
                vertices[offset + j] = color.get(j - 2);
            }

            // Texture Coords
            for (int j = 6; j < TEXTURE_COORDS_SIZE + 6; j++) {
                vertices[offset + j] = textureCoords[i].get(j - 6);
            }

            // Texture ID
            vertices[offset + 8] = textureId;

            // Entity ID
            vertices[offset + 9] = spriteRenderer.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        int[] elements = new int[INDICES_PER_QUAD * maxBatchSize];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = INDICES_PER_QUAD * index;
        int offset = VERTICES_PER_QUAD * index;

        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;
        // Triangle 2
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public int getNumOfSpriteRenderers() {
        return numOfSpriteRenderers;
    }

    public boolean hasSpriteRenderersRoom() {
        return renderersArrayHasRoom;
    }

    public boolean hasTextureRoom() {
        return textures.size() < TEXTURE_LIMIT;
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }

    public int getZIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(zIndex, o.getZIndex());
    }
}
