#version 430
out vec4 color;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
in vec2 textureCoordinate;
layout(binding = 0)  uniform sampler2D samp;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main(void) {
        vec4 textureColor = texture2D(samp, textureCoordinate);
        float luminance = dot(textureColor.rgb, W);
        color = vec4(vec3(luminance), textureColor.a);
}