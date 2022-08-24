#!/bin/bash
set -e

#ANDROID_NDK_PATH=<your_path_to>/android-ndk-r17c
ANDROID_NDK_PATH=~/tools/android-ndk-r17c/

TOOLCHAIN_FILE=${ANDROID_NDK_PATH}/build/cmake/android.toolchain.cmake

# for 32bit android
# mkdir build_android_v7a; cd build_android_v7a
# cmake -DCMAKE_TOOLCHAIN_FILE=$TOOLCHAIN_FILE -DANDROID_ABI="armeabi-v7a" -DCMAKE_ANDROID_NDK=$ANDROID_NDK_PATH -DANDROID_PLATFORM=android-23 -DANDROID_STL=c++_static ..
# make -j7

# for 64 bit android
rm -rf build_android_v8a; mkdir build_android_v8a; cd build_android_v8a
cmake -DCMAKE_SYSTEM_NAME="Android" -DCMAKE_TOOLCHAIN_FILE=$TOOLCHAIN_FILE -DANDROID_ABI="arm64-v8a" -DCMAKE_ANDROID_NDK=$ANDROID_NDK_PATH -DANDROID_PLATFORM=android-23 -DANDROID_STL=c++_static ..
make -j7

make install
cd -

cp run_demo.sh install/rknn_pass_through_demo_Android/
