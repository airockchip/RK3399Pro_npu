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
