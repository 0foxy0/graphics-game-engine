package de.foxy.engine.renderer;

import de.foxy.engine.utils.ShaderPreset;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int programId = Integer.MIN_VALUE, vertexId = Integer.MIN_VALUE, fragmentId = Integer.MIN_VALUE;
    private String vertexSrc, fragmentSrc;
    private final String filePath;
    private boolean isBeingUsed = false;

    public Shader(String filePath) {
        this.filePath = filePath;
        construct(filePath);
    }

    public Shader(ShaderPreset shaderPreset) {
        this.filePath = shaderPreset.getAbsolutePath();
        construct(filePath);
    }

    /**
     * Loads the default shader from the engine (ShaderPreset.DEFAULT)
     */
    public Shader() {
        this.filePath = ShaderPreset.DEFAULT.getAbsolutePath();
        construct(filePath);
    }

    private void construct(String filePath) {
        String vertexShaderKey = "vertex";
        String fragmentShaderKey = "fragment";

        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern pattern = Pattern.compile("#type\\s+(\\w+)([\\s\\S]*?)(?=(#type|$))");

            Matcher matcher = pattern.matcher(source);
            HashMap<String, String> shaderMap = new HashMap<>();

            while (matcher.find()) {
                String type = matcher.group(1).toLowerCase();
                String content = matcher.group(2).trim();
                shaderMap.put(type, content);
            }

            if (!shaderMap.containsKey(vertexShaderKey) || !shaderMap.containsKey(fragmentShaderKey)) {
                throw new IllegalStateException("File does not contain vertex or fragment shader");
            }

            vertexSrc = shaderMap.get(vertexShaderKey);
            fragmentSrc = shaderMap.get(fragmentShaderKey);
        } catch (IOException e) {
            throw new AssertionError("Couldn't open shader file: " + filePath);
        }
    }

    public void compileAndLink() {
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSrc);
        glCompileShader(vertexId);

        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int logLength = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            throw new AssertionError("Vertex shader compilation failed\n" + filePath + "\n" + glGetShaderInfoLog(vertexId, logLength));
        }

        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSrc);
        glCompileShader(fragmentId);

        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int logLength = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            throw new AssertionError("Fragment shader compilation failed\n" + filePath + "\n" + glGetShaderInfoLog(fragmentId, logLength));
        }

        programId = glCreateProgram();
        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);
        glLinkProgram(programId);

        success = glGetProgrami(programId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int logLength = glGetProgrami(programId, GL_INFO_LOG_LENGTH);
            throw new AssertionError("Shader program link failed\n" + filePath + "\n" + glGetProgramInfoLog(programId, logLength));
        }
    }

    public void use() {
        if (isBeingUsed) {
            return;
        }
        glUseProgram(programId);
        isBeingUsed = true;
    }

    public void detach() {
        if (!isBeingUsed) {
            return;
        }
        glUseProgram(0);
        isBeingUsed = false;
    }

    public boolean isCompiledAndLinked() {
        return programId != Integer.MIN_VALUE && vertexId != Integer.MIN_VALUE && fragmentId != Integer.MIN_VALUE;
    }

    public void uploadMat4f(String variableName, Matrix4f mat4) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a matrix4 should be uploaded");
            use();
        }

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4 * 4);
        mat4.get(matBuffer);

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniformMatrix4fv(variableLocation, false, matBuffer);
    }

    public void uploadMat3f(String variableName, Matrix3f mat3) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a matrix3 should be uploaded");
            use();
        }

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(3 * 3);
        mat3.get(matBuffer);

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniformMatrix3fv(variableLocation, false, matBuffer);
    }

    public void uploadVec4f(String variableName, Vector4f vec4) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a vector4 should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform4f(variableLocation, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3f(String variableName, Vector3f vec3) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a vector3 should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform3f(variableLocation, vec3.x, vec3.y, vec3.z);
    }

    public void uploadVec2f(String variableName, Vector2f vec2) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a vector2 should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform2f(variableLocation, vec2.x, vec2.y);
    }

    public void uploadFloat(String variableName, float value) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a float should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform1f(variableLocation, value);
    }

    public void uploadInt(String variableName, int value) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but an Integer should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform1i(variableLocation, value);
    }

    public void uploadTexture(String variableName, int slot) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a Texture should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform1i(variableLocation, slot);
    }

    public void uploadIntArray(String variableName, int[] arr) {
        if (!isBeingUsed) {
            //throw new IllegalStateException("Shader is not being used but a Texture should be uploaded");
            use();
        }

        int variableLocation = glGetUniformLocation(programId, variableName);
        glUniform1iv(variableLocation, arr);
    }

    public String getFilePath() {
        return filePath;
    }
}
