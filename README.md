# RKNPU For RK3399Pro
本工程主要为Rockchip RK3399Pro提供NPU驱动、相关示例等。


## 适用平台
- RK3399Pro

*注意：*  
- 适用于RK1808/RV1109/RV1126的驱动和C API请参考：https://github.com/rockchip-linux/rknpu  
- 适用于RK3566/RK3568/RK3588/RV1103/RV1106的驱动和C API请参考：https://github.com/rockchip-linux/rknpu2  


## RKNN Toolkit

在使用RKNN API进行部署之前，需要使用RKNN Toolkit或RKNN Toolkit2将原始的模型转成RKNN模型。
- RK1808/RK1806/RV1109/RV1126/RK3399Pro 使用： https://github.com/rockchip-linux/rknn-toolkit  
- RK3566/RK3568/RK3588/RV1103/RV1106 使用：https://github.com/rockchip-linux/rknn-toolkit2  
    
具体的使用说明请参考相应的网址。

## NPU驱动说明

### NPU驱动目录说明

RK3399Pro的NPU驱动被封装在NPU的boot.img文件中。RK3399Pro更新NPU驱动时，只要替换相应的boot.img等文件即可。

不同的RK3399Pro开发板可能通过不同的方式（PCIE和USB 3.0）和NPU通信，所使用的boot.img等NPU固件也不同。

驱动目录主要包含如下内容
```
drivers/
├── npu_firmware
│   ├── npu_fw
│   └── npu_pcie_fw
└── npu_transfer_proxy
    ├── android-arm64-v8a
    ├── android-armeabi-v7a
    ├── linux-aarch64
    └── linux-arm
```
- npu_firmware: 存放NPU固件 
  - npu_fw_pcie: 适用于PCIE接口的NPU固件，包括boot.img, MiniLoaderAll.bin, trust.img, uboot.img等。
  - npu_fw: 适用于USB接口的NPU固件，包括boot.img, MiniLoaderAll.bin, trust.img, uboot.img等。
- npu_transfer_proxy: 存放适用不同操作系统的npu_transfer_proxy。AI应用和NPU通信时需要依赖npu_transfer_proxy服务。
  
*通过在板端执行npu_transfer_proxy devices命令，可以确认NPU连接方式是PCIE还是USB*  
```
# 如果结果如下，使用的是USB
/ # npu_transfer_proxy devices
List of ntb devices attached
2010fcfcde48fafd    80f3eb90    USB_DEVICE

# 如果结果如下，使用的是PCIE
rk3399pro:/ $ npu_transfer_proxy devices
List of ntb devices attached
0123456789ABCDEF    cfbc0c55    PCIE
```

### 手动更新NPU驱动

在RK3399Pro上更新NPU驱动是通过更新NPU相关的boot.img等文件实现的。具体的更新方法如下：

- 更新PCIE接口NPU:
```
# 如果是Android系统的固件，在更新前先获取root和读写权限，如果是Linux系统的固件，跳过这两条命令
adb shell root
adb shell remount
# 更新boot.img等
adb push npu_firmware/npu_pcie_fw/* /vendor/etc/npu_fw/
adb shell reboot
```

- 更新USB接口NPU:
```
# 如果是Android系统的固件，在更新前先获取root和读写权限，如果是Linux系统的固件，跳过这两条命令
adb shell root
adb shell remount
# 更新boot.img等
adb push npu_firmware/npu_fw/* /vendor/etc/npu_fw/
adb shell reboot
```

***注意: 不同的RK3399Pro固件，其npu_fw的路径可能不同，在更新boot.img等文件前建议先确认下该文件夹的位置。***

### npu_transfer_proxy使用说明
AI应用调用NPU进行模型初始化、推理等操作时需要依赖npu_transfer_proxy服务进行通信。  
在运行AI应用前需要确认是否已启动npu_transfer_proxy服务。  
默认RK3399Pro的固件会自动启动npu_transfer_proxy服务，确认方法如下：
```
/ # ps -ef |grep npu_transfer_proxy
  268 root      252m S    ./usr/bin/npu_transfer_proxy
```
如果能找到对应的进程，则代表该服务已启动。如果找不到对应的进程，可以手动启动该程序，并后台运行，或者将该程序添加到开机启动服务中。  
手动启动npu_transfer_proxy并后台运行的命令如下：
```
./npu_transfer_proxy &
```

## RKNN C API
RKNN C API的详细使用说明请参考[rknn-api/doc](rknn-api/doc)中的使用指南文档。
