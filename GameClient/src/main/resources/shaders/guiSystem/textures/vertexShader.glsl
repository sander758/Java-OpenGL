#version 400 core

in vec2 in_position;

out vec2 textureCoords;

uniform mat4 transformationMatrix;

void main() {
    gl_Position = transformationMatrix * vec4(in_position, 0.0, 1.0);

    textureCoords = vec2(in_position.x / 2.0 + 0.5, in_position.y / 2.0 + 0.5);
}
