package com.rockchip.gpadc.ssddemo;


import android.util.Log;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by randall on 18-4-27.
 */


/*
* 一个简单的bufferqueue实现
*
* */
public class ImageBufferQueue {
    private ImageBuffer[] mQueueBuffer;
    private int mQueueBufferSize;
    private int mCurrentFreeBufferIndex;
    private int mCurrentUsingBufferIndex;

    public ImageBufferQueue(int bufferSize, int width, int height) {
        mCurrentFreeBufferIndex = -1;
        mCurrentUsingBufferIndex = -1;
        mQueueBufferSize = bufferSize;
        mQueueBuffer = new ImageBuffer[mQueueBufferSize];

        for (int i=0; i<mQueueBufferSize; ++i) {
            mQueueBuffer[i] = new ImageBuffer(width, height);
        }
    }

    public void release() {
        for (int i=0; i<mQueueBufferSize; ++i) {
            mQueueBuffer[i] = null;
        }

        mQueueBuffer = null;
    }

    //获取待处理的数据
    public synchronized ImageBuffer getReadyBuffer() {

        int index = mCurrentUsingBufferIndex;

        for (int i=0; i<mQueueBufferSize; ++i) {
            ++index;

            if (index >= mQueueBufferSize) {
                index = 0;
            }

            if (mQueueBuffer[index].mStatus == ImageBuffer.STATUS_READY) {
                break;
            }
        }

        if ((index != mCurrentUsingBufferIndex) && (mQueueBuffer[index].mStatus == ImageBuffer.STATUS_READY)) {
            mCurrentUsingBufferIndex = index;
            mQueueBuffer[index].mStatus = ImageBuffer.STATUS_USING;

            return mQueueBuffer[index];
        }

        return null;
    }

    public synchronized void releaseBuffer(ImageBuffer buffer) {
        buffer.mStatus = ImageBuffer.STATUS_INVAILD;
    }

    public  synchronized ImageBuffer getFreeBuffer() {

        int index = mCurrentFreeBufferIndex;

        for (int i=0; i<mQueueBufferSize; ++i) {
            ++index;

            if (index >= mQueueBufferSize) {
                index = 0;
            }

            if (mQueueBuffer[index].mStatus != ImageBuffer.STATUS_USING) {
                break;
            }
        }

        mCurrentFreeBufferIndex = index;

        mQueueBuffer[index].mStatus = ImageBuffer.STATUS_INVAILD;
        return mQueueBuffer[index];
    }
    public synchronized void postBuffer(ImageBuffer buffer) {
        buffer.mStatus = ImageBuffer.STATUS_READY;
    }

    public class ImageBuffer {
        static public final int STATUS_INVAILD = 0;
        static public final int STATUS_READY = 1;
        static public final int STATUS_USING = 2;

        public int mTextureId;  //textureId
        public int mFramebuffer;
        public int mStatus;
        public int mWidth;
        public int mHeight;

        public ImageBuffer(int width, int height) {
            mStatus = STATUS_INVAILD;
            mWidth = width;
            mHeight = height;
            mTextureId = -1;
            prepareFrameBuffer(width, height);
        }

        public void finalize() {
            if (mTextureId >= 0) {
                int[] values = new int[1];
                values[0] = mFramebuffer;
                glDeleteFramebuffers(1, values, 0);
                InferenceWrapper.delete_direct_texture(mTextureId);
            }
        }

        private void prepareFrameBuffer(int width, int height) {
            int[] values = new int[1];

            mTextureId = InferenceWrapper.create_direct_texture(width, height, GL_RGB);

            glBindTexture(GL_TEXTURE_2D, mTextureId);

            // Set parameters.  We're probably using non-power-of-two dimensions, so
            // some values may not be available for use.
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                    GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
                    GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
                    GL_CLAMP_TO_EDGE);


            // Create framebuffer object and bind it.
            glGenFramebuffers(1, values, 0);
            mFramebuffer = values[0];    // expected > 0
            glBindFramebuffer(GL_FRAMEBUFFER, mFramebuffer);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                    GL_TEXTURE_2D, mTextureId, 0);

            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                throw new IllegalStateException();
            }

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }
}
