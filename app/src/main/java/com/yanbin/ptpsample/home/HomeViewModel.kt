package com.yanbin.ptpsample.home

import android.hardware.usb.UsbDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanbin.ptp.camera.CameraEvent
import com.yanbin.ptp.camera.CameraImage
import com.yanbin.ptp.camera.IPtpCamera
import com.yanbin.ptpsample.home.usecase.PtpUsecase
import com.yanbin.ptpsample.usb.UsbDeviceItem
import com.yanbin.ptpsample.usb.UsbDeviceRepository
import com.yanbin.ptpsample.usb.UsbPermissionHelper
import com.yanbin.ptpsample.usb.UsbPermissionListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val usbDeviceRepository: UsbDeviceRepository,
    private val ptpUsecase: PtpUsecase,
    permissionHelper: UsbPermissionHelper,
): ViewModel() {
    val usbDevices = usbDeviceRepository.getUsbDevices()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, emptyList())

    private val _cameraFlow = MutableStateFlow<IPtpCamera?>(null)

    private val isCameraConnected: StateFlow<Boolean> = _cameraFlow.map { camera ->
        camera != null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _showSimpleDialog = MutableStateFlow<String?>(null)
    val showSimpleDialog = _showSimpleDialog.asStateFlow()

    val cameraName: StateFlow<String> = _cameraFlow.map { camera ->
        camera?.getName() ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val images: StateFlow<List<CameraImage>> = _cameraFlow.map { camera ->
        camera?.getCameraImages() ?: emptyList()
    }.flatMapConcat { images ->
        images.asFlow()
    }
        .map { image ->
            val downloadThumbnailFile = ptpUsecase.downloadThumbnail(_cameraFlow.value!!, image)
            image.copy(thumbUrl = downloadThumbnailFile.absolutePath)
        }
        .scan(emptyList<CameraImage>()) { acc, value ->
            acc + value
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _capturedImage: MutableStateFlow<CameraImage?> = MutableStateFlow(null)
    val capturedImage: StateFlow<CameraImage?> = _capturedImage.asStateFlow()

    init {
        usbDeviceRepository.setUsbPermissionHelper(permissionHelper)
    }

    fun onDeviceSelected(usbDeviceItem: UsbDeviceItem) {
        if (isCameraConnected.value) {
            _showSimpleDialog.value = "相機已連線"
            return
        }

        if (usbDeviceItem.hasPermission) {
            connectToCamera(usbDeviceItem)
        } else {
            usbDeviceRepository.requestPermission(usbDeviceItem, object : UsbPermissionListener {
                override fun onPermissionGranted(usbDevice: UsbDevice) {
                    connectToCamera(usbDeviceItem)
                }

                override fun onPermissionDenied() {
                    // TODO: Show error message
                }
            })
        }
    }

    private fun connectToCamera(usbDeviceItem: UsbDeviceItem) {
        viewModelScope.launch {
            kotlin.runCatching {
                val camera = ptpUsecase.createCameraDevice(usbDeviceItem)
                listenToCameraEvents(camera)
                _cameraFlow.value = camera
            }.onFailure {
                Timber.e(it, "Failed to connect to device as camera")
                _showSimpleDialog.value = "連線失敗，請確認該裝置是否為相機"
            }
        }
    }

    private fun listenToCameraEvents(camera: IPtpCamera) {
        viewModelScope.launch {
            if (camera.isEventModeSupported()) {
                camera.setEventMode(true)
                // Start a looper to listen to camera events
                while (true) {
                    val events = camera.getEvents()
                    events.forEach { event ->
                        when (event) {
                            is CameraEvent.ObjectAddedEvent -> {
                                val image = camera.getCameraImage(event.objectId)
                                if (image != null) {
                                    val imageFile = ptpUsecase.downloadImage(camera, image)
                                    _capturedImage.value = image.copy(sourceUrl = imageFile.absolutePath)
                                }
                            }
                            else -> {}
                        }
                    }
                    delay(1000)
                }
            }
        }
    }

    fun onSimpleDialogDismissClicked() {
        viewModelScope.launch {
            _showSimpleDialog.value = null
        }
    }
}