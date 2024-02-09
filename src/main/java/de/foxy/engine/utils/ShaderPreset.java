package de.foxy.engine.utils;

import java.io.File;

public enum ShaderPreset {
    TWOD("default"),
    DEFAULT(TWOD);

    private final String fileName;

    ShaderPreset(String fileName) {
        this.fileName = fileName;
    }
    ShaderPreset(ShaderPreset shaderPreset) {
        fileName = shaderPreset.getFileName();
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return new File("src/main/java/de/foxy/engine/assets/shaders/"+ fileName +".glsl").getAbsolutePath();
    }
}
