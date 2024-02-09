package de.foxy.engine.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

public class PickingTexture {
    private int width, height;
    private int textureId, fboId, depthTextureId;

    public PickingTexture(int width, int height) {
        this.width = width;
        this.height = height;

        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        glEnable(GL_TEXTURE_2D);
        depthTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);

        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Framebuffer is not complete");
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboId);
    }

    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        return (int) (pixels[0]) - 1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
