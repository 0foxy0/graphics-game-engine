package de.foxy.engine.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final String filePath;
    private transient final int textureId;
    private final int width, height;
    private boolean pixelate;

    public Texture(String filePath, boolean pixelate) {
        this.filePath = filePath;
        this.pixelate = pixelate;

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // blur or pixelate when getting larger
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, pixelate ? GL_NEAREST : GL_LINEAR);
        // blur or pixelate when getting smaller
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, pixelate ? GL_NEAREST : GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

        if (image == null) {
            throw new IllegalStateException("Couldn't load image via stb: " + filePath);
        }

        if (!Arrays.asList(3, 4).contains(channels.get(0))) {
            throw new IllegalArgumentException("Unknown number of channels: " + channels.get(0));
        }

        this.width = width.get(0);
        this.height = height.get(0);

        int rgbOrRgba = channels.get(0) == 4 ? GL_RGBA : GL_RGB;

        glTexImage2D(GL_TEXTURE_2D, 0, rgbOrRgba, width.get(0), height.get(0), 0, rgbOrRgba, GL_UNSIGNED_BYTE, image);
        stbi_image_free(image);
    }
    public Texture(int width, int height, boolean pixelate) {
        this.width = width;
        this.height = height;
        this.pixelate = pixelate;
        filePath = "GENERATED";
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);

        // blur or pixelate when getting larger
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, pixelate ? GL_NEAREST : GL_LINEAR);
        // blur or pixelate when getting smaller
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, pixelate ? GL_NEAREST : GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public String getFilePath() {
        return filePath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return textureId;
    }

    public boolean doesPixelate() {
        return pixelate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Texture texture)) {
            return false;
        }
        return texture.getWidth() == width && texture.getHeight() == height && texture.getId() == textureId && texture.getFilePath().equals(filePath) && texture.doesPixelate() == pixelate;
    }
}
