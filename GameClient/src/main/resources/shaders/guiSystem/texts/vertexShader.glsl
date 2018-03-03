#version 400 core

in vec2 in_position;
in vec2 textureCoordinates;

out vec2 passTextureCoordinates;

uniform mat4 transformationMatrix;

void main() {
    gl_Position = transformationMatrix * vec4(in_position, 0.0, 1.0);

    passTextureCoordinates = textureCoordinates;
}
