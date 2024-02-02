package de.foxy.engine.renderer;

import de.foxy.engine.Window;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.FMath;
import de.foxy.engine.utils.geometry.Box2D;
import de.foxy.engine.utils.geometry.Circle;
import de.foxy.engine.utils.geometry.Line2D;
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
    private static final int POSITION_SIZE = 3, COLOR_SIZE = 3;
    private static final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
    private static final int VERTEX_SIZE_IN_BYTES = VERTEX_SIZE * Float.BYTES;
    // Offsets
    private static final int POSITION_OFFSET = 0;
    private static final int COLOR_OFFSET = POSITION_SIZE * Float.BYTES;

    private static final int VERTICES_PER_LINE = 2;
    private static final int LINES_PER_CIRCLE = 20;

    private static DebugDraw activeInstance = null;

    private final Shader shader = AssetCollector.getShader("src/main/java/de/foxy/engine/assets/shaders/debug-line2d.glsl");
    private int MAX_LINES = 50;
    private ArrayList<Line2D> lines = new ArrayList<>(MAX_LINES);
    private float[] vertexArray = new float[MAX_LINES * VERTEX_SIZE * VERTICES_PER_LINE];
    private int vaoId, vboId;

    private boolean initialized = false;
    private float lineWidth = 1;

    public DebugDraw() {
        activeInstance = this;
    }

    public DebugDraw(int maxLines) {
        MAX_LINES = maxLines;
        lines = new ArrayList<>(MAX_LINES);
        vertexArray = new float[MAX_LINES * VERTEX_SIZE * VERTICES_PER_LINE];
        activeInstance = this;
    }

    public DebugDraw(float lineWidth) {
        this.lineWidth = lineWidth;
        activeInstance = this;
    }

    public DebugDraw(int maxLines, float lineWidth) {
        MAX_LINES = maxLines;
        lines = new ArrayList<>(MAX_LINES);
        vertexArray = new float[MAX_LINES * VERTEX_SIZE * VERTICES_PER_LINE];
        this.lineWidth = lineWidth;
        activeInstance = this;
    }

    private static void init() {
        activeInstance.shader.compileAndLink();

        activeInstance.vaoId = glGenVertexArrays();
        glBindVertexArray(activeInstance.vaoId);

        activeInstance.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, activeInstance.vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) activeInstance.vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glLineWidth(activeInstance.lineWidth);

        activeInstance.initialized = true;
    }

    private static void beginFrame() {
        if (!activeInstance.initialized) {
            init();
        }

        for (int i = 0; i < activeInstance.lines.size(); i++) {
            if (activeInstance.lines.get(i).beginFrame() < 0) {
                activeInstance.lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (activeInstance == null) {
            return;
        }

        beginFrame();
        if (activeInstance.lines.isEmpty()) {
            return;
        }

        int i = 0;
        for (Line2D line : activeInstance.lines) {
            for (int j = 0; j < 2; j++) {
                Vector2f position = j == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                activeInstance.vertexArray[i] = position.x;
                activeInstance.vertexArray[i + 1] = position.y;
                activeInstance.vertexArray[i + 2] = -10;

                activeInstance.vertexArray[i + 3] = color.x;
                activeInstance.vertexArray[i + 4] = color.y;
                activeInstance.vertexArray[i + 5] = color.z;

                i += VERTEX_SIZE;
            }
        }
        int actualVerticesSize = activeInstance.lines.size() * VERTEX_SIZE * VERTICES_PER_LINE;

        glBindBuffer(GL_ARRAY_BUFFER, activeInstance.vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(activeInstance.vertexArray, 0, actualVerticesSize));

        activeInstance.shader.use();
        activeInstance.shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        activeInstance.shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(activeInstance.vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, actualVerticesSize);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        activeInstance.shader.detach();
    }

    public void addLine2D(Line2D line) {
        if (lines.size() >= MAX_LINES) {
            return;
        }
        lines.add(line);
    }

    public void addBox2D(Box2D box) {
        if (lines.size() + 4 >= MAX_LINES) {
            return;
        }
        Vector2f min = new Vector2f(box.getCenter()).sub(new Vector2f(box.getDimensions()).div(2));
        Vector2f max = new Vector2f(box.getCenter()).add(new Vector2f(box.getDimensions()).div(2));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (box.getRotation() != 0) {
            for (Vector2f vertex : vertices) {
                FMath.rotate(vertex, box.getRotation(), box.getCenter());
            }
        }

        for (int i = 0; i < vertices.length; i++) {
            int nextIndex = (i + 1) % vertices.length;
            addLine2D(new Line2D(vertices[i], vertices[nextIndex], box.getColor(), box.getLifetime()));
        }
    }

    public void addCircle(Circle circle) {
        if (lines.size() + LINES_PER_CIRCLE >= MAX_LINES) {
            return;
        }

        Vector2f[] points = new Vector2f[LINES_PER_CIRCLE];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(circle.getRadius(), 0);
            FMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(circle.getCenter());

            if (i > 0) {
                addLine2D(new Line2D(points[i - 1], points[i], circle.getColor(), circle.getLifetime()));
            }

            currentAngle += increment;
        }

        addLine2D(new Line2D(points[points.length - 1], points[0], circle.getColor(), circle.getLifetime()));
    }
}
