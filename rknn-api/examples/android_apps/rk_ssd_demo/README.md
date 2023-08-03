# 简介
 - rk_ssd_demo 是3399pro上如何调用NPU的demo，该demo的基础模型是ssd_inception_v2

# 使用说明
 - 可以使用android studio编译该工程
 - ssd.rknn是使用rknn toolkit将ssd_inception_v2.pb转换而来，具体转换方法参考RKNN Toolkit的参考文档

# 代码说明
## 代码分为三大部分：
 - JAVA: com.rockchip.gpadc.ssddemo: 读取camera输入，并调用jni进行inference，请将结果显示出来
 - JAVA: com.rockchip.gdapc.demo.glhelper: 封装的opengl处理函数，用于2D纹理渲染,格式转换等，主要目的是降低CPU使用率．
 - JNI: 调用rknn_api进行实际inference
