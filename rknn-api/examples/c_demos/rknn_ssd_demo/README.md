# Example of ssd_mobilenet_v1

## Model Source

### Original model
The model comes from TensorFlow's official model zoo:
https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf1_detection_zoo.md

### Convert to RKNN model
Please refer to the example in the RKNN Toolkit project to generate the RKNN model:
https://github.com/rockchip-linux/rknn-toolkit/tree/master/examples/tensorflow/ssd_mobilenet_v1

Note:
- Change the target platform of the RKNN model through the target paramter of test script.

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

The test result should be similar to picutre `ref_detect_result.bmp`.  
Reference labels, coordinates, and scores:
```
person @ (16 125 57 205) 0.988352
person @ (209 115 256 221) 0.972152
person @ (113 118 151 189) 0.943352
car @ (143 132 214 173) 0.888194
bicycle @ (173 157 280 233) 0.855247
car @ (137 124 163 145) 0.791216
person @ (81 132 91 156) 0.573479
car @ (0 136 12 154) 0.463060
person @ (96 135 108 163) 0.426520
```

- Different systems, different versions of tools and drivers may have slightly different results.
