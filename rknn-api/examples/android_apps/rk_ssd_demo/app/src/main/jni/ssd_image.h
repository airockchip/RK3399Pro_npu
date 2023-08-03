#ifndef SSD_IMAGE_SSD_IMAGE_H
#define SSD_IMAGE_SSD_IMAGE_H

#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "rkssd4j", ##__VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "rkssd4j", ##__VA_ARGS__);


namespace ssd_image {

void create(int inputSize, int channel, int numResult, int numClasses, char *mParamPath);
void destroy();
bool run_ssd(char *inData, float *y0, float *y1);
bool run_ssd(int texId, float *y0, float *y1);

}  // namespace label_image


#endif  //SSD_IMAGE_SSD_IMAGE_H

