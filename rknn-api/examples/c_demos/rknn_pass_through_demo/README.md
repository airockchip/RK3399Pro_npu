# Example of pass through input data


## Model Source

### Original model
The model used in this example come from the TensorFlow Lite offical model zoo:
https://github.com/tensorflow/models/blob/master/research/slim/nets/mobilenet_v1.md

### Convert to RKNN model
Please refer to the example in the RKNN Toolkit project to generate the RKNN model:
https://github.com/rockchip-linux/rknn-toolkit/tree/master/examples/tflite/mobilenet_v1

Note:
- Change the target platform of the RKNN model through the target paramter of test script.
- Modify the test scipt to adjust the quantization data type. Set the parameter `do_quantization` of the `build` interface to False to generate a floating-point RKNN model. Modify the `quantized_dtype` parameter in the `config` interface to generate quantized RKNN models of different data types.

### Export pre-compiled RKNN model
Please refer to the example in the RKNN Toolkit project to export the pre-compiled RKNN model:
https://github.com/rockchip-linux/rknn-toolkit/tree/master/examples/common_function_demos/export_rknn_precompile_model

Note:
- Please specify the correct target platform when exporting the pre-compiled RKNN model.


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
adb push install/rknn_pass_through_demo_Linux /userdata/
# for Android
adb push install/rknn_pass_through_demo_Android /userdata/
```

*Note: for Android, you may need execute command below to get write premissions for file system.*  

```
adb root
adb remount
```

## run

```
adb shell
cd /userdata/rknn_pass_through_demo_Linux/
./run_demo.sh
```

## Expected results

This example will print the TOP5 labels and corresponding scores of the test image classification results.
- The float RKNN model inference commands and reference results are as follows：
```
./run_rv1109_rv1126.sh fp

 --- Top5 ---
156: 0.947754
155: 0.047180
205: 0.003340
284: 0.000781
260: 0.000104
```
- The int16 quantized RKNN model inference commands and reference results are as follows:
```
./run_rv1109_rv1126.sh int16

 --- Top5 ---
156: 0.948242
155: 0.047150
205: 0.003283
284: 0.000778
260: 0.000107
```
- The int8 quantized RKNN model inference commands and reference results are as follows:
```
./run_rv1109_rv1126.sh int8

 --- Top5 ---
156: 0.769043
155: 0.220337
205: 0.004036
284: 0.002449
260: 0.001157
```
- The uint8 quantized RKNN model inference commands and reference results are as follows:
```
./run_rv1109_rv1126.sh uint8

 --- Top5 ---
156: 0.853516
155: 0.091980
205: 0.013626
284: 0.006485
194: 0.002020
```

1. The label index with the highest score is 156, the corresponding label is `Pekinese, Pekingese, Peke`.
2. Because the image library used is different, the preprocessing methods such as scaling are also slightly different, so the inference results of C Demo will be slightly different from those of Python Demo.
3. There will be some differences in the results obtained by different quantized data types.
4. Different platforms, different versions of tools and drivers may have slightly different results.
