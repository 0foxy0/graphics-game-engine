package de.foxy.engine.renderer;

import de.foxy.engine.Window;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.Line2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    // Sizes
    private final int POSITION_SIZE = 3, COLOR_SIZE = 3;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
    private final int VERTEX_SIZE_IN_BYTES = VERTEX_SIZE * Float.BYTES;
    // Offsets
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_SIZE * Float.BYTES;

    private final int VERTICES_PER_LINE = 2;
    private int MAX_LINES = 50;

    private ArrayList<Line2D> lines = new ArrayList<>(MAX_LINES);

    private float[] vertexArray = new float[MAX_LINES * VERTEX_SIZE * VERTICES_PER_LINE];
    private final Shader shader = AssetCollector.getShader("src/main/java/de/foxy/engine/assets/shaders/debug-line2d.glsl");
    private int vaoId, vboId;

    private boolean initialized = false;
    private float lineWidth = 1;

    public DebugDraw() {}
    public DebugDraw(int maxLines) {
        MAX_LINES = maxLines;
        lines = new ArrayList<>(MAX_LINES);
        vertexArray = new float[MAX_LINES * VERTEX_SIZE * VERTICES_PER_LINE];
    }
    public DebugDraw(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    public DebugDraw(int maxLines, float lineWidth) {
        MAX_LINES = maxLines;
        this.lineWidth = lineWidth;
    }

    public void init() {
        shader.compileAndLink();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glLineWidth(lineWidth);

        initialized = true;
    }

    public void beginFrame() {
        if (!initialized) {
            init();
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public void draw() {
        beginFrame();
        if (lines.isEmpty()) {
            return;
        }

        int i = 0;
        for (Line2D line : lines) {
            for (int j = 0; j < 2; j++) {
                Vector2f position = j == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                vertexArray[i] = position.x;
                vertexArray[i + 1] = position.y;
                vertexArray[i + 2] = -10;

                vertexArray[i + 3] = color.x;
                vertexArray[i + 4] = color.y;
                vertexArray[i + 5] = color.z;

                i += VERTEX_SIZE;
            }
        }
        int actualVerticesSize = lines.size() * VERTEX_SIZE * VERTICES_PER_LINE;

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, actualVerticesSize));

        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, actualVerticesSize);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    public void addLine2D(Line2D line) {
        if (lines.size() >= MAX_LINES) {
            return;
        }
        lines.add(line);
    }
}
