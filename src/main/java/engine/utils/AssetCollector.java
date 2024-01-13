package engine.utils;

import engine.components.SpriteSheet;
import engine.renderer.Shader;
import engine.renderer.Texture;

import java.io.File;
import java.util.HashMap;

public class AssetCollector {
    private static HashMap<String, Shader> shaders = new HashMap<>();
    private static HashMap<String, Texture> textures = new HashMap<>();
    private static HashMap<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String shaderFilePath) {
        File file = new File(shaderFilePath);

        if (shaders.containsKey(file.getAbsolutePath())) {
            return shaders.get(file.getAbsolutePath());
        }

        Shader shader = new Shader(shaderFilePath);

        shaders.put(file.getAbsolutePath(), shader);
        return shader;
    }

    public static Texture getTexture(String textureFilePath, boolean pixelate) {
        File file = new File(textureFilePath);
        String mapKey = file.getAbsolutePath() + pixelate;

        if (shaders.containsKey(mapKey)) {
            return textures.get(mapKey);
        }

        Texture texture = new Texture(textureFilePath, pixelate);

        textures.put(mapKey, texture);
        return texture;
    }

    public static void addSpriteSheet(SpriteSheet spriteSheet) {
        if (spriteSheets.containsKey(spriteSheet.getUUID())) {
            return;
        }

        spriteSheets.put(spriteSheet.getUUID(), spriteSheet);
    }

    public static SpriteSheet getSpriteSheet(String spriteSheetId) {
        if (!spriteSheets.containsKey(spriteSheetId)) {
            throw new AssertionError("No SpriteSheet with ID: "+ spriteSheetId +" stored");
        }

        return spriteSheets.get(spriteSheetId);
    }
}
