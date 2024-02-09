#type vertex
#version 460 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoords;
layout (location = 3) in float aTextureId;
layout (location = 4) in float aEntityId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;
out float fTextureId;
out float fEntityId;

void main() {
    gl_Position = uProjection * uView * vec4(aPos, 1.0, 1.0);
    fColor = aColor;
    fTextureCoords = aTextureCoords;
    fTextureId = aTextureId;
    fEntityId = aEntityId;
}


#type fragment
#version 460 core

uniform sampler2D uTextures[8];

in vec4 fColor;
in vec2 fTextureCoords;
in float fTextureId;
in float fEntityId;

out vec3 color;

void main() {
    vec4 textureColor = vec4(1, 1, 1, 1);

    if (fTextureId > 0) {
        int id = int(fTextureId);
        textureColor = fColor * texture(uTextures[id], fTextureCoords);
    }

    if (textureColor.a < 0.5) {
        discard;
    }

    color = vec3(fEntityId, fEntityId, fEntityId);
}