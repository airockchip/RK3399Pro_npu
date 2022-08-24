#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 v_TextureCoordinates;

uniform samplerExternalOES s_texture;


void main() {
    gl_FragColor = texture2D(s_texture, v_TextureCoordinates);
}
