// a = attribute | exmpl: aPos = postition attribute
// f = fragment | exmpl: fColor = color fragment
// u = uniform | exmpl: uView = view uniform

#type vertex
#version 460 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoords;
layout (location = 3) in float aTextureId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;
out float fTextureId;

void main() {
    gl_Position = uProjection * uView * vec4(aPos, 1.0, 1.0);
    fColor = aColor;
    fTextureCoords = aTextureCoords;
    fTextureId = aTextureId;
}


#type fragment
#version 460 core

uniform sampler2D uTextures[8];

in vec4 fColor;
in vec2 fTextureCoords;
in float fTextureId;

out vec4 color;

void main() {
    if (fTextureId > 0) {
        color = fColor * texture(uTextures[int(fTextureId)], fTextureCoords);
        return;
    }
    color = fColor;
}