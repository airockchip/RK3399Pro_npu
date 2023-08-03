
#include <jni.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <pthread.h>
#include <sys/syscall.h>


#include <sched.h>

#include "ssd_native_c_api.h"
#include "ssd_image.h"
#include "direct_texture.h"



static char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);

    if (alen > 0) {
        rtn = new char[alen + 1];
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}



JNIEXPORT jint JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_init
  (JNIEnv *env, jobject obj, jint inputSize, jint channel, jint numResult, jint numClasses, jstring modelPath)
{
	char *mModelPath = jstringToChar(env, modelPath);
	ssd_image::create(inputSize, channel, numResult, numClasses, mModelPath);

	return 0;
}

JNIEXPORT void JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_native_1deinit
		(JNIEnv *env, jobject obj) {
	ssd_image::destroy();

}

JNIEXPORT jint JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_native_1run___3B_3F_3F
  (JNIEnv *env, jobject obj, jbyteArray in, jfloatArray out0, jfloatArray out1) {


  	jboolean inputCopy = JNI_FALSE;
  	jbyte* const inData = env->GetByteArrayElements(in, &inputCopy);

 	jboolean outputCopy = JNI_FALSE;

  	jfloat* const y0 = env->GetFloatArrayElements(out0, &outputCopy);
	jfloat* const y1 = env->GetFloatArrayElements(out1, &outputCopy);

	ssd_image::run_ssd((char *)inData, (float *)y0, (float *)y1);

	env->ReleaseByteArrayElements(in, inData, JNI_ABORT);
	env->ReleaseFloatArrayElements(out0, y0, 0);
	env->ReleaseFloatArrayElements(out1, y1, 0);


	return 0;
}

JNIEXPORT jint JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_native_1run__I_3F_3F
  (JNIEnv *env, jobject obj, jint texId, jfloatArray out0, jfloatArray out1) {

 	jboolean outputCopy = JNI_FALSE;

  	jfloat* const y0 = env->GetFloatArrayElements(out0, &outputCopy);
	jfloat* const y1 = env->GetFloatArrayElements(out1, &outputCopy);

	ssd_image::run_ssd((int)texId, (float *)y0, (float *)y1);

	env->ReleaseFloatArrayElements(out0, y0, 0);
	env->ReleaseFloatArrayElements(out1, y1, 0);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_native_1create_1direct_1texture
  (JNIEnv *env, jclass obj, jint width, jint height, jint fmt) {
	return (jint)gDirectTexture.createDirectTexture((int)width, (int)height, (int)fmt);
}

JNIEXPORT jboolean JNICALL Java_com_rockchip_gpadc_ssddemo_InferenceWrapper_native_1delete_1direct_1texture
  (JNIEnv *env, jclass obj, jint texId) {
	return (jboolean)gDirectTexture.deleteDirectTexture((int)texId);
}

