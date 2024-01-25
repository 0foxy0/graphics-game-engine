package engine.components;

import engine.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;

public class SpriteSheet {
    private Texture texture;
    private ArrayList<Sprite> sprites = new ArrayList<>();
    private int numOfSprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numOfSprites, int spacing) {
        this.texture = texture;
        this.numOfSprites = numOfSprites;

        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        for (int i = 0; i < numOfSprites; i++) {
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] textureCoords = new Vector2f[]{
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            Sprite sprite = new Sprite(texture, textureCoords);
            sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }

    public String getFilePath() {
        return texture.getFilePath();
    }

    public int getNumOfSprites() {
        return numOfSprites;
    }
}
