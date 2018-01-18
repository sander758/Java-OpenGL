#version 400 core

in vec3 finalColor;
in float passBrightness;

out vec4 out_color;

uniform vec3 lightColor;

void main() {
    vec3 diffuse = passBrightness * lightColor;

    out_color = vec4(diffuse, 1.0) * vec4(finalColor, 0);
}
