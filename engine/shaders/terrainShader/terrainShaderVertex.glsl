#version 400 core
// *********************************************************************************************************************

in vec3 in_position;
in vec3 in_color;
in vec3 in_normal;

out vec3 passColor;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec4 passShadowCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 lightDirection;

uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float transistionDistance = 5.0;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(in_position.xyz, 1.0);
    passShadowCoords = toShadowMapSpace * worldPosition;
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    passColor = in_color;
    surfaceNormal = (transformationMatrix * vec4(in_normal, 0.0)).xyz;
    toLightVector = -lightDirection;


    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    float distance = length(positionRelativeToCam.xyz);

    distance = distance - (shadowDistance - transistionDistance);
    distance = distance / transistionDistance;
    passShadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);


}
