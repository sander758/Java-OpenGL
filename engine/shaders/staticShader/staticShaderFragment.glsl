#version 400 core
// *********************************************************************************************************************

in vec3 finalColor;
in float passBrightness;
in vec4 shadowCoords;

out vec4 out_Color;

uniform vec3 lightColor;
uniform sampler2D shadowMap;

void main(void) {
    float objectNearestLight = texture(shadowMap, shadowCoords.xy).r;
    float lightFactor = 1;
    if (shadowCoords.z > objectNearestLight) {
        lightFactor = 1.0 - (shadowCoords.w * 0.4);
    }


    vec3 diffuse = passBrightness * lightColor;
//    vec3 diffuse = passBrightness * lightColor * lightFactor;

    out_Color = vec4(diffuse, 1.0) * vec4(finalColor, 0);
//    out_Color = vec4(shadowCoords.z);
}