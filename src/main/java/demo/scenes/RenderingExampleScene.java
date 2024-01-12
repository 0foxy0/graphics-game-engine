package demo.scenes;

import engine.Scene;
import engine.renderer.Shader;
import engine.renderer.Texture;
import engine.utils.ShaderPreset;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderingExampleScene extends Scene {
    private Shader textureShader;
    private Texture testTexture;
    // vertex array object; vertex buffer object; element buffer object
    private int vaoId, vboId, eboId;
    private final float[] vertexArr = {
         // pos                 color                    UV coordinates
            100f,   0f, 0f,     1.0f, 0.0f, 0.0f, 1.0f,  1.0f, 0.0f,     // Bottom right 0
              0f, 100f, 0f,     0.0f, 1.0f, 0.0f, 1.0f,  0.0f, 1.0f,     // Top left     1
            100f, 100f, 0f,     1.0f, 0.0f, 1.0f, 1.0f,  1.0f, 1.0f,     // Top right    2
              0f,   0f, 0f,     1.0f, 1.0f, 0.0f, 1.0f,  0.0f, 0.0f,     // Bottom left  3
    };
    // Must be in counter-clockwise order
    private final int[] elementArr = {
            2, 1, 0,
            0, 1, 3,
    };

    @Override
    public void start() {
        testTexture = new Texture("src/main/java/demo/assets/image.png", true);
        textureShader = new Shader(ShaderPreset.TEXTURE_3D);
        textureShader.compileAndLink();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArr.length);
        // correct the order which the vbo must be for the gpu
        vertexBuffer.put(vertexArr).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArr.length);
        // correct the order which the ebo must be in for the gpu
        elementBuffer.put(elementArr).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int floatSizeInBytes = Float.BYTES;
        int vertexSizeInBytes = (positionSize + colorSize + uvSize) * floatSizeInBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionSize * floatSizeInBytes);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (positionSize + colorSize) * floatSizeInBytes);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(double deltaTime) {
        textureShader.use();

        textureShader.uploadTexture("uTexture", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        textureShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        textureShader.uploadMat4f("uView", camera.getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArr.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        testTexture.unbind();
        textureShader.detach();
    }
}
