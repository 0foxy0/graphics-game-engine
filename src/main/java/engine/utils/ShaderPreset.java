package engine.utils;

public enum ShaderPreset {
    TWOD("2d"),
    THREED("3d"),
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
        return "src/main/java/engine/assets/shaders/"+ fileName +".glsl";
    }
}
