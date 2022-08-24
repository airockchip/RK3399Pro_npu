/* 
 * Create By randall.zhuo@rock-chips.com
 *
 * 2018/10/30
 *
 * OpenGL 2D texture helper.
 *
 */

#ifndef MY_DIRECT_TEXTURE_HOOK_H
#define MY_DIRECT_TEXTURE_HOOK_H

#define EGL_EGLEXT_PROTOTYPES
#define GL_GLEXT_PROTOTYPES
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include "android/hardware_buffer.h"

#include <list>

struct _DirectTexture {
	GLuint texId;
    AHardwareBuffer* textureBuffer;
	EGLClientBuffer clientBuffer;
	EGLImageKHR img;
	bool locked;
	char *pixels; // GraphicBuffer
	char *data;
	int texWidth;
	int texHeight;
	int format;
	int bytePerPixel;
	int stride;
};

class DirectTexture {
public:
	DirectTexture();
	~DirectTexture();

	/*
 	 * Desc:
 	 *    Create a direct 2D texture which use eglCreateImageKHR and GraphicBuffer.
 	 *
 	 * Param:
 	 *    texWidth:  texture width 
 	 *    texHeight: texture height
 	 *    format: GL color format. Only support GL_RGB GL_RGBA now.
 	 *
 	 *  Return:
 	 *    >=0:  texture id
 	 *    -1: unsupport format 
 	 *    -2: EGL_NO_DISPLAY
 	 *    -3: EGL_NO_IMAGE_KHR
 	 * */

	int createDirectTexture(uint32_t texWidth, uint32_t texHeight, int format);
	
	/*
 	* Desc:
 	*    Delete the texture created by 'createDirectTexture'
 	*
 	* Param:
 	*    texId:  texture id
 	* */
	bool deleteDirectTexture(int texId);

	/*
    Desc:
        Get the buffer of texture, you must call releaseBufferByTexId() when you do not need it.

    Param:
        texId:  the texture id from createDirectTexture()

	*/

	char* requireBufferByTexId(int texId);

	/*
    Desc:
        release texture buffer. After you call requireBufferByTexId()

    Param:
        texId:  the texture id from createDirectTexture()
	*/

	bool releaseBufferByTexId(int texId);

private:
    uint32_t glColorFmtToHalFmt(int fmt);
    int getBytePerPixel(int fmt);
    _DirectTexture * getDirectTexture(int texId);

    std::list<_DirectTexture *> dtList;
};

extern DirectTexture  gDirectTexture;

#endif
