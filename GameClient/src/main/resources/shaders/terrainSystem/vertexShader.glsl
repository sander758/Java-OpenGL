#version 400 core

const float transistionDistance = 5.0;

in vec3 in_position;
in vec3 in_normal;
in vec3 in_color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 lightDirection;

uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

out vec3 passColor;
out vec3 surfaceNormal;
out vec4 worldPos;
out vec4 passShadowCoords;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(in_position, 1.0);
    worldPos = worldPosition;
    passShadowCoords = toShadowMapSpace * worldPosition;

    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    passColor = in_color;
    surfaceNormal = (transformationMatrix * vec4(in_normal, 0.0)).xyz;

    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    float distance = length(positionRelativeToCam.xyz);

    distance = distance - (shadowDistance - transistionDistance);
    distance = distance / transistionDistance;
    passShadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
}
