#version 400 core
// *********************************************************************************************************************

in vec3 in_position;
in vec3 in_color;
in vec3 in_normal;

out vec3 passColor;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec4 worldPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 lightDirection;


void main() {
    vec4 worldPosition = transformationMatrix * vec4(in_position.xyz, 1.0);
    worldPos = worldPosition;

    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCam;

    passColor = in_color;
    surfaceNormal = (transformationMatrix * vec4(in_normal, 0.0)).xyz;
    toLightVector = -lightDirection;
}
