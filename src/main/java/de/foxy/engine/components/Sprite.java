package de.foxy.engine.components;

import de.foxy.engine.renderer.Texture;
import org.joml.Vector2f;

public class Sprite {
    private int width, height;
    private Texture texture;
    private Vector2f[] textureCoords = new Vector2f[]{
            new Vector2f(1,1),
            new Vector2f(1,0),
            new Vector2f(0,0),
            new Vector2f(0,1)
    };

    public Sprite(Texture texture) {
        this.texture = texture;
    }

    public Sprite(Texture texture, Vector2f[] textureCoords, int width, int height) {
        this.texture = texture;
        this.textureCoords = textureCoords;
        this.width = width;
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getTextureId() {
        return texture == null ? -1 : texture.getId();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }
}
