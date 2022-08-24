/**
 * Created by Randall on 2018/10/15
 * <p>
 * RKNN inference Camera Demo
 */

package com.rockchip.gpadc.ssddemo;

import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.rockchip.gdapc.demo.glhelper.TextureProgram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.MODE_PRIVATE;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glFinish;
import static android.opengl.GLES20.glViewport;
import static com.rockchip.gpadc.ssddemo.PostProcess.INPUT_SIZE;
import static java.lang.Thread.sleep;
import com.rockchip.gpadc.ssddemo.InferenceResult.Recognition;


public class CameraSurfaceRender implements GLSurfaceView.Renderer {
    public static final String TAG = "ssd";
    private String mModelName = "ssd.rknn";

    private Camera mCamera;

    private SurfaceTexture mSurfaceTexture;

    private TextureProgram mTextureProgram;     // Draw texture2D (include camera texture (GL_TEXTURE_EXTERNAL_OES) and normal GL_TEXTURE_2D texture)
//    private LineProgram mLineProgram;           // Draw detection result
    private GLSurfaceView mGLSurfaceView;
    private int mOESTextureId = -1;    //camera texture ID

    // for inference
    private InferenceWrapper mInferenceWrapper;
    private String fileDirPath;     // file dir to store model cache
    private ImageBufferQueue mImageBufferQueue;    // intermedia between camera thread and  inference thread
    private InferenceResult mInferenceResult = new InferenceResult();  // detection result
    private int mWidth;    //surface width
    private int mHeight;    //surface height
    private Handler mMainHandler;   // ui thread handle,  update fps
    private Object cameraLock = new Object();
    private volatile boolean mStopInference = false;


    public CameraSurfaceRender(GLSurfaceView glSurfaceView, Handler handler) {
        mGLSurfaceView = glSurfaceView;
        mMainHandler = handler;
        fileDirPath = mGLSurfaceView.getContext().getCacheDir().getAbsolutePath();
        createFile(mModelName, R.raw.ssd);

        try {
            mInferenceResult.init(mGLSurfaceView.getContext().getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        startCamera();
        startTrack();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    private void startTrack() {
        mInferenceResult.reset();
        mImageBufferQueue = new ImageBufferQueue(3, INPUT_SIZE, INPUT_SIZE);
        mOESTextureId = TextureProgram.createOESTextureObject();
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        mTextureProgram = new TextureProgram(mGLSurfaceView.getContext());
//        mLineProgram = new LineProgram(mGLSurfaceView.getContext());

        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.requestRender();
            }
        });

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mStopInference = false;
        mInferenceThread = new Thread(mInferenceRunnable);
        mInferenceThread.start();
    }


    @Override
    public void onDrawFrame(GL10 gl) {

        if (mStopInference) {
            return;
        }

        ImageBufferQueue.ImageBuffer imageBuffer = mImageBufferQueue.getFreeBuffer();

        if (imageBuffer == null) {
            return;
        }

        // render to offscreen
        glBindFramebuffer(GL_FRAMEBUFFER, imageBuffer.mFramebuffer);
        glViewport(0, 0, imageBuffer.mWidth, imageBuffer.mHeight);
        mTextureProgram.drawFeatureMap(mOESTextureId);
        glFinish();
        mImageBufferQueue.postBuffer(imageBuffer);

        // main screen
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, mWidth, mHeight);
        mTextureProgram.draw(mOESTextureId);

//        mLineProgram.draw(recognitions);

        mSurfaceTexture.updateTexImage();

        // update main screen
        // draw track result
        updateMainUI(1, 0);
    }

    private void updateMainUI(int type, Object data) {
        Message msg = mMainHandler.obtainMessage();
        msg.what = type;
        msg.obj = data;
        mMainHandler.sendMessage(msg);
    }

    public ArrayList<Recognition> getTrackResult(){
        return mInferenceResult.getResult();
    }

    public void onPause() {
        stopCamera();
        stopTrack();

    }

    public void onResume() {
        startCamera();
    }

    private void stopTrack() {

        mStopInference = true;
        try {
            mInferenceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mSurfaceTexture != null) {
            int[] t = {mOESTextureId};
            GLES20.glDeleteTextures(1, t, 0);

            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mTextureProgram != null) {
            mTextureProgram.release();
            mTextureProgram = null;
        }

//        if (mLineProgram != null) {
//            mLineProgram.release();
//            mLineProgram = null;
//        }

        if (mImageBufferQueue != null) {
            mImageBufferQueue.release();
            mImageBufferQueue = null;
        }
    }

    private void startCamera() {
        if (mCamera != null) {
            return;
        }

        synchronized (cameraLock) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();

            int numCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, camInfo);
                //if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(i);
                break;
                //}
            }

            if (mCamera == null) {
                throw new RuntimeException("Unable to open camera");
            }

            Camera.Parameters camParams = mCamera.getParameters();

            List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
            for (int i = 0; i < sizes.size(); i++) {
                Camera.Size size = sizes.get(i);
                Log.v(TAG, "Camera Supported Preview Size = " + size.width + "x" + size.height);
            }

            camParams.setPreviewSize(640, 480);
            camParams.setRecordingHint(true);

            mCamera.setParameters(camParams);

            if (mSurfaceTexture != null) {
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCamera.startPreview();
            }
        }
    }

    private void stopCamera() {
        if (mCamera == null)
            return;

        synchronized (cameraLock) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        Log.i(TAG, "stopped camera");
    }

    private Thread mInferenceThread;
    private Runnable mInferenceRunnable = new Runnable() {
        public void run() {

            int count = 0;
            long oldTime = System.currentTimeMillis();
            long currentTime;

            String paramPath = fileDirPath + "/" + mModelName;
            mInferenceWrapper = new InferenceWrapper(INPUT_SIZE, PostProcess.INPUT_CHANNEL, PostProcess.NUM_RESULTS, PostProcess.NUM_CLASSES, paramPath);
            while (!mStopInference) {
                ImageBufferQueue.ImageBuffer buffer = mImageBufferQueue.getReadyBuffer();

                if (buffer == null) {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                InferenceResult.OutputBuffer outputs = mInferenceWrapper.run(buffer.mTextureId);

                mInferenceResult.setResult(outputs);

                mImageBufferQueue.releaseBuffer(buffer);

                if (++count >= 30) {
                    currentTime = System.currentTimeMillis();

                    float fps = count * 1000.f / (currentTime - oldTime);

                    //Log.d(TAG, "current fps = " + fps);

                    oldTime = currentTime;
                    count = 0;
                    updateMainUI(0, fps);

                }

            }

            mInferenceWrapper.deinit();
            mInferenceWrapper = null;
        }
    };

    private void createFile(String fileName, int id) {
        String filePath = fileDirPath + "/" + fileName;
        try {
            File dir = new File(fileDirPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 目录存在，则将apk中raw中的需要的文档复制到该目录下
            File file = new File(filePath);

            if (!file.exists() || isFirstRun()) {

                InputStream ins = mGLSurfaceView.getContext().getResources().openRawResource(id);// 通过raw得到数据资源
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[8192];
                int count = 0;

                while ((count = ins.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                ins.close();

                Log.d(TAG, "Create " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstRun() {
        SharedPreferences sharedPreferences = mGLSurfaceView.getContext().getSharedPreferences("setting", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }

        return isFirstRun;
    }
}
