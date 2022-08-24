package com.rockchip.gpadc.ssddemo;

/**
 * Created by randall on 18-4-18.
 */

public class InferenceWrapper {
    static {
        System.loadLibrary("rkssd4j");
    }

    InferenceResult.OutputBuffer mOutputs;

    /*
    *  params:
    *       inputSize: 输入图像大小
    *       channel： 图像通道
    *       numResult： 结果数量
    *       numClasses: SSD分类数
    *       modelPath: 模型路径
    * */
    public InferenceWrapper(int inputSize, int channel, int numResult, int numClasses, String modelPath) {

        mOutputs = new InferenceResult.OutputBuffer();

        mOutputs.mLocations = new float[numResult * 4];
        mOutputs.mClasses = new float[numResult * numClasses];

        init(inputSize, channel, numResult, numClasses, modelPath);
    }

    public void deinit() {
        native_deinit();
        mOutputs.mLocations = null;
        mOutputs.mClasses = null;
        mOutputs = null;

    }
    /*
    *  params:
    *       inData: 原始图像数据，image参数要和init中的一致
    *  return:
    *      返回检测结果
    * */
    public InferenceResult.OutputBuffer run(byte[] inData) {
        native_run(inData, mOutputs.mLocations, mOutputs.mClasses);
        return  mOutputs;
    }

    /*
     *  params:
     *       textureId: 纹理ID， 大小 300x300 格式 RGBA
     *  return:
     *      返回检测结果
     *      locations还需要后处理才是真正的坐标，具体参考PostProcess.java
     *      confidence, confidence还需要做expit处理才是真正的得分，具体参考PostProcess.java
     * */
    public InferenceResult.OutputBuffer run(int textureId) {
        native_run(textureId, mOutputs.mLocations, mOutputs.mClasses);
        return  mOutputs;
    }


    public static  int create_direct_texture(int texWidth, int texHeight, int format) {
        return native_create_direct_texture(texWidth, texHeight, format);
    }

    public static boolean delete_direct_texture(int texId) {
        return native_delete_direct_texture(texId);
    }

    private native int init(int inputSize, int channel, int numResult, int numClasses, String modelPath);
    private native void native_deinit();

    private native int native_run(byte[] inData, float[] outputLocations, float[] outputClasses);

    /*
     *  descption:
     *       检测, 只适用于Android平台
     *  params:
     *       textureId:      输入图像纹理Id
     *       outputLocations:    用于保存预测框位置(xmin, ymin, xmax, ymax)(需要后处理，具体参考PostProcess.java)
     *       outputClasses:  用于保存confidence, confidence还需要做expit处理((float) (1. / (1. + Math.exp(-x)));)
     * */
    private native int native_run(int textureId, float[] outputLocations, float[] outputClasses);

    private static native int native_create_direct_texture(int texWidth, int texHeight, int format);

    private static native boolean native_delete_direct_texture(int texId);
}