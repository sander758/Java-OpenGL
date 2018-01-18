#version 400 core

const vec3 waterColor = vec3(0.604, 0.867, 0.851);
const float fresnelReflective = 0.9;
const float edgeSoftness = 0.3;
const float minBlueness = 0.4;
const float maxBlueness = 0.75;
const float murkyDepth = 5;

out vec4 out_Color;

in vec4 pass_clipSpaceReal;
in vec3 pass_toCameraVector;
in vec3 pass_normal;
in vec4 pass_clipSpaceGrid;
in vec3 pass_specular;
in vec3 pass_diffuse;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D depthTexture;
uniform vec2 nearFarPlanes;

vec3 applyMurkiness(vec3 refractColor, float waterDepth) {
    float murkyFactor = smoothstep(0, murkyDepth, waterDepth);
    float murkiness = minBlueness + murkyFactor * (maxBlueness - minBlueness);
    return mix(refractColor, waterColor, murkiness);
}

float toLinearDepth(float zDepth) {
    float near = nearFarPlanes.x;
    float far = nearFarPlanes.y;
    return 2.0 * near * far / (far + near - (2.0 * zDepth - 1.0) * (far - near));
}

float calculateWaterDepth(vec2 texCoords) {
    float depth = texture(depthTexture, texCoords).r;
    float floorDistance = toLinearDepth(depth);
    depth = gl_FragCoord.z;
    float waterDistance = toLinearDepth(depth);
    return floorDistance - waterDistance;
}

float calculateFresnel() {
    vec3 viewVector = normalize(pass_toCameraVector);
    vec3 normal = normalize(pass_normal);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, fresnelReflective);
    return clamp(refractiveFactor, 0.0, 1.0);
}

vec2 clipSpaceToTexCoords(vec4 clipSpace) {
    vec2 ndc = (clipSpace.xy / clipSpace.w);
    vec2 texCoords = ndc / 2.0 + 0.5;
    return clamp(texCoords, 0.002, 0.998);
}

void main() {

    vec2 texCoordsReal = clipSpaceToTexCoords(pass_clipSpaceReal);
    vec2 texCoordsGrid = clipSpaceToTexCoords(pass_clipSpaceGrid);

    vec2 reflectionTexCoords = vec2(texCoordsGrid.x, 1 - texCoordsGrid.y);
    vec2 refractionTexCoords = texCoordsGrid;
    float waterDepth = calculateWaterDepth(texCoordsReal);

    vec3 reflectColor = texture(reflectionTexture, reflectionTexCoords).rgb;
    vec3 refractColor = texture(refractionTexture, refractionTexCoords).rgb;

    reflectColor = mix(reflectColor, waterColor, minBlueness);
    refractColor = applyMurkiness(refractColor, waterDepth);


    vec3 finalColor = mix(reflectColor, refractColor, calculateFresnel());
    finalColor = finalColor * pass_diffuse + pass_specular;

    out_Color = vec4(finalColor, 1.0);
    out_Color.a = clamp(waterDepth / edgeSoftness, 0.0, 1.0);
}
