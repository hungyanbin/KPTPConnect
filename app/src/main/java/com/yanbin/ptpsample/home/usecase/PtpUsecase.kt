package com.yanbin.ptpsample.home.usecase

import com.yanbin.ptp.camera.CameraImage
import com.yanbin.ptp.camera.IPtpCamera
import com.yanbin.ptpsample.usb.UsbDeviceItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PtpUsecase {

    suspend fun createCameraDevice(deviceItem: UsbDeviceItem): IPtpCamera

    suspend fun downloadImage(camera: IPtpCamera, cameraImage: CameraImage): File

    suspend fun downloadThumbnail(camera: IPtpCamera, cameraImage: CameraImage): File

    suspend fun getCameraConnectionStatus(camera: IPtpCamera): Flow<Boolean>
}