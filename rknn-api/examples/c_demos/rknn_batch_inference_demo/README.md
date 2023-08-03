# Example of rknn batch size

## Model Source

### Original model
The model used in this example come from the TensorFlow Lite offical model zoo:
https://github.com/tensorflow/models/blob/master/research/slim/nets/mobilenet_v1.md

### Convert to RKNN model
Please refer to the example in the RKNN Toolkit project to generate the RKNN model:
hhttps://github.com/rockchip-linux/rknn-toolkit/tree/master/examples/common_function_demos/batch_size
*Note: set `rknn_batch_size` in `build` interface.*


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
adb push install/rknn_batch_inference_demo_Linux /userdata/
# for Android
adb push install/rknn_batch_inference_demo_Android /userdata/
```

*Note: for Android, you may need execute command below to get write premissions for file system.*  

```
adb root
adb remount
```

## run

```
adb shell
cd /userdata/rknn_batch_inference_demo_Linux/
./run_demo.sh
```

## Expected results

This example will print the TOP5 labels and corresponding scores of the test image classification results. For example, the inference results of this example are as follows:
```
 --- Top5 ---
156: 0.851074
155: 0.091736
205: 0.013588
284: 0.007191
194: 0.002014
 --- Top5 ---
156: 0.851074
155: 0.091736
205: 0.013588
284: 0.007191
194: 0.002014
 --- Top5 ---
156: 0.851074
155: 0.091736
205: 0.013588
284: 0.007191
194: 0.002014
 --- Top5 ---
156: 0.851074
155: 0.091736
205: 0.013588
284: 0.007191
194: 0.002014
```

1. The label index with the highest score is 156, the corresponding label is `Pekinese, Pekingese, Peke`.
2. Because the image library used is different, the preprocessing methods such as scaling are also slightly different, so the inference results of C Demo will be slightly different from those of Python Demo.
3. The top-5 perdictions of 4 inputs should be exactly the same.
4. Different systems, different versions of tools and drivers may have slightly different results.
