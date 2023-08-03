# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(jni_srcs
        src/main/jni/ssd_native_c_api.cc
        src/main/jni/ssd_image.cc
        src/main/jni/direct_texture.cc
        )

add_library(rkssd4j SHARED ${jni_srcs})


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

set(link_libs ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/librknn_api.so
              EGL
              GLESv2
              android
              ${log-lib}
)

target_link_libraries( # Specifies the target library.
                       rkssd4j
                       ${link_libs} )