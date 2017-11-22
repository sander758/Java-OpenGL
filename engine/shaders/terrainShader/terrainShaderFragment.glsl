#version 400 core
// *********************************************************************************************************************

in vec3 finalColor;
in float passBrightness;
in vec4 shadowCoords;

out vec4 out_Color;

uniform vec3 lightColor;
uniform sampler2D shadowMap;
uniform float mapSize;

// percentage closer filtering
const int pcfCount = 3;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void main(void) {
    float texelSize = 1.0 / mapSize;
    float total = 0.0;

    for (int x = -pcfCount; x <= pcfCount; x++) {
        for (int y = -pcfCount; y <= pcfCount; y++) {
            float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
            if (shadowCoords.z > objectNearestLight + 0.002) {
                total += 1.0;
            }
        }
    }
    total /= totalTexels;
    float lightFactor = 1.0 - (total * 0.6 * shadowCoords.w);


    vec3 diffuse = passBrightness * lightColor;

    out_Color = vec4(diffuse * lightFactor, 1.0) * vec4(finalColor, 0);
}