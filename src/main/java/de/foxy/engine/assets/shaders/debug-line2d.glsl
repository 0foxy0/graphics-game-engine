#type vertex
#version 460 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;

uniform mat4 uProjection;
uniform mat4 uView;

out vec3 fColor;

void main() {
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
    fColor = aColor;
}


#type fragment
#version 460 core

in vec3 fColor;

out vec4 color;

void main() {
    color = vec4(fColor, 1.0);
}