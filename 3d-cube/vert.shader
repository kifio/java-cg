#version 430
layout(location = 0)  in vec3 position;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
uniform float tf;

out vec4 variyingColor;

mat4 buildRotateX(float rad);
mat4 buildRotateY(float rad);
mat4 buildRotateZ(float rad);
mat4 buildTranslate(float x, float y, float z);

void main(void) {
    float i = gl_InstanceID + tf;
    float a = sin(2.0 * i) * 8.0;
    float b = sin(3.0 * i) * 8.0;
    float c = sin(4.0 * i) * 8.0;

    mat4 localRotX = buildRotateX(1000 * i);
    mat4 localRotY = buildRotateX(1000 * i);
    mat4 localRotZ = buildRotateX(1000 * i);
    mat4 localTrans = buildTranslate(a, b, c);

    mat4 localModelMatrix = m_matrix * localTrans * localRotX * localRotY * localRotZ;
    mat4 mv_matrix = v_matrix * localModelMatrix;

    gl_Position = proj_matrix * mv_matrix * vec4(position, 1.0);
    variyingColor = vec4(position, 1.0) * 0.5 + vec4(0.5, 0.5, 0.5, 0.5);
}

mat4 buildTranslate(float x, float y, float z) {
    return mat4(
    1.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    x, y, z, 1.0);
}

mat4 buildRotateX(float rad) {
    return mat4(
    1.0, 0.0, 0.0, 0.0,
    0.0, cos(rad), -sin(rad), 0.0,
    0.0, sin(rad), cos(rad), 0.0,
    0.0, 0.0, 0.0, 1.0);
}

mat4 buildRotateY(float rad) {
    return mat4(
    cos(rad), 0.0, sin(rad), 0.0,
    0.0, 1.0, 0.0, 0.0,
    -sin(rad), 0.0, cos(rad), 0.0,
    0.0, 0.0, 0.0, 1.0);
}

mat4 buildRotateZ(float rad) {
    return mat4(
    cos(rad), -sin(rad), 0.0, 0.0,
    sin(rad), cos(rad), 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0);
}

mat4 buildScale(float x, float y, float z) {
    return mat4(
    x, 0.0, 0.0, 0.0,
    0.0, y, 0.0, 0.0,
    0.0, 0.0, z, 0.0,
    0.0, 0.0, 0.0, 1.0);
}