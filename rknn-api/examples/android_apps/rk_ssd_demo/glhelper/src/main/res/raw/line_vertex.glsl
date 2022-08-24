precision  lowp float;

attribute vec2 a_Position;


void main(void)
{
    vec2 pos = a_Position;
    gl_Position = vec4(pos, 0, 1);
}