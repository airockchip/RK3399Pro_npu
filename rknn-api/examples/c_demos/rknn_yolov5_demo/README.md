# Yolo-v5 demo

## Model souce

The original model used in this demo is yolov5s_relu.pt, which is included in the convert_rknn_demo/yolov5/models directory. Compared to the original version, some post-processing has been removed from the model and moved to the outside for better inference performance. The silu activation function has been replaced with relu. For more information on creating a YOLOv5 model without post-processing, please refer to [https://github.com/airockchip/yolov5/blob/master/README_rkopt_manual.md. ↗](https://github.com/airockchip/yolov5/blob/master/README_rkopt_manual.md)

For more information about other YOLO models, please refer to [https://github.com/airockchip/rknn_model_zoo/tree/main/models/CV/object_detection/yolo. ↗](https://github.com/airockchip/rknn_model_zoo/tree/main/models/CV/object_detection/yolo)



## Convert model to RKNN

You can use the convert_rknn_demo/yolov5/models/pytorch2rknn.py script to get the RKNN model. The following special parameters can be specified during conversion:

- Set output_optimize to 1 to reduce the time it takes for the capi normal inference interface rknn_outputs_get. (conflicts with zero-copy, cannot be used together)
- When force_builtin_perm is set to True, the generated model input changed to nhwc instead of nchw, which is more suitable for image input. (Most images are in hwc format)


## Notice

1. Use rknn-toolkit version 1.7.0 or higher.
2. This demo only supports inference of rknn models with 8-bit asymmetric quantization.
3. When switching to your own trained model, please pay attention to aligning post-processing parameters such as anchor, otherwise it may cause post-processing parsing errors.
4. The official yolov5 website and rk pre-trained models detect 80 classes of targets. If you are using your own trained model, modify the OBJ_CLASS_NUM and NMS_THRESH post-processing parameters in include/postprocess.h before compiling.
5. Due to hardware limitations, the post-processing part of the yolov5 model is moved to the CPU for this demo's model by default. The models included in this demo use relu as the activation function. Compared with the silu activation function, the accuracy is slightly reduced, but the inference speed is faster.
6. Regarding loading time: the models in the model directory are pre-compiled rknn models, which load faster than non-pre-compiled rknn models. The conversion script in the convert_rknn_demo directory generates non-pre-compiled rknn models. If you need to re-generate pre-compiled rknn models, please refer to [example of export rknn pre-compile model](https://github.com/rockchip-linux/rknn-toolkit/tree/master/examples/common_function_demos/export_rknn_precompile_model).


## build

- for Linux:  
modify `GCC_COMPILER` on `build_linux.sh` for target platform, then execute  

```
./build_linux.sh
```

- for Android:  
modify `ANDROID_NDK_PATH` on `build_android.sh` to your NDK path, then execute  

```
./build_android.sh
```

## install

connect device and push build output into `/userdata`  

```
# for Linux
adb push install/rknn_ssd_demo_Linux /userdata/
# for Android
adb push install/rknn_ssd_demo_Android /userdata/
```

*Note: for Android, you may need execute command below to get write premissions for file system.*  

```
adb root
adb remount
```

## run

```
adb shell
cd /userdata/rknn_ssd_demo_Linux/
./run_demo.sh
```

## Expected results

## Expected results

The test result should be similar to picutre `ref_detect_result.bmp`.  
Reference labels, coordinates, and scores:
```
person @ (208 246 287 505) 0.867932
person @ (481 240 560 525) 0.860113
person @ (109 234 231 536) 0.860113
bus @ (90 126 553 464) 0.562063
```

- Different platforms, different versions of tools and drivers may have slightly different results.
