#version 400 core

in vec2 passTextureCoordinates;

out vec4 out_color;

uniform sampler2D fontAtlas;
uniform vec3 color;

void main() {
//    out_color = vec4(1.0, 0.0, 0.0, 1);
    out_color = vec4(color, texture(fontAtlas, passTextureCoordinates).a);
}
