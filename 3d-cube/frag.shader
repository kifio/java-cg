#version 430
out vec4 color;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
in vec4 variyingColor;
void main(void) {
        color = variyingColor;
}