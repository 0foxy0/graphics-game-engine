package engine.renderer;

import engine.Window;
import engine.components.SpriteRenderer;
import engine.utils.AssetCollector;
import engine.utils.ShaderPreset;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    private final int POSITION_SIZE = 2, COLOR_SIZE = 4, POSITION_OFFSET = 0;
    // 6 indices per quad/rectangle; 3 indices per triangle
    private final int VERTICES_PER_QUAD = 4, INDICES_PER_QUAD = 6;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int VERTEX_SIZE_IN_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] spriteRenderers;
    private int numOfSpriteRenderers = 0;
    private boolean renderersArrayHasRoom = true;
    private float[] vertices;

    private int vaoId, vboId;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];

        this.vertices = new float[maxBatchSize * VERTICES_PER_QUAD * VERTEX_SIZE];

        this.shader = AssetCollector.getShader(ShaderPreset.DEFAULT.getAbsolutePath());
        this.shader.compileAndLink();
    }

    public RenderBatch(int maxBatchSize, Shader shader) {
        this.maxBatchSize = maxBatchSize;
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];

        this.vertices = new float[maxBatchSize * VERTICES_PER_QUAD * VERTEX_SIZE];

        this.shader = shader;
        this.shader.compileAndLink();
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
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.use();

        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numOfSpriteRenderers * INDICES_PER_QUAD, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        shader.detach();
    }

    public void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        int index = numOfSpriteRenderers;
        spriteRenderers[index] = spriteRenderer;
        numOfSpriteRenderers++;

        loadVertexProperties(index);

        if (numOfSpriteRenderers >= maxBatchSize) {
            renderersArrayHasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = spriteRenderers[index];
        Vector4f color = spriteRenderer.getColor();

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

            vertices[offset] = spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x);
            vertices[offset + 1] = spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y);

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

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

    public boolean isFull() {
        return !renderersArrayHasRoom;
    }
}
