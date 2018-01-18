#version 400 core

in vec3 in_position;
in vec3 in_normal;
in vec3 in_color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 lightDirection;

out vec3 passColor;
out vec3 surfaceNormal;
out vec4 worldPos;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(in_position, 1.0);
    worldPos = worldPosition;

    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    passColor = in_color;
    surfaceNormal = (transformationMatrix * vec4(in_normal, 0.0)).xyz;
}
