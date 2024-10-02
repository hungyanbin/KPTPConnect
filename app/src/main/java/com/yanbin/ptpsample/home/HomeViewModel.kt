package com.yanbin.ptpsample.home

import android.hardware.usb.UsbDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanbin.ptpsample.home.usecase.PtpUsecase
import com.yanbin.ptpsample.usb.UsbDeviceItem
import com.yanbin.ptpsample.usb.UsbDeviceRepository
import com.yanbin.ptpsample.usb.UsbPermissionListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val usbDeviceRepository: UsbDeviceRepository,
    private val ptpUsecase: PtpUsecase
): ViewModel() {
    val usbDevices = usbDeviceRepository.getUsbDevices()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, emptyList())

    private val isCameraConnected: StateFlow<Boolean> = MutableStateFlow(false)

    private val _showSimpleDialog = MutableStateFlow<String?>(null)
    val showSimpleDialog = _showSimpleDialog.asStateFlow()

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
                _showSimpleDialog.value = "連線成功 ${camera.getName()}"
//                dashboard.bindAndConnect(camera)
            }.onFailure {
                Timber.e(it, "Failed to connect to device as camera")
                _showSimpleDialog.value = "連線失敗，請確認該裝置是否為相機"
            }
        }
    }

    fun onSimpleDialogDismissClicked() {
        viewModelScope.launch {
            _showSimpleDialog.value = null
        }
    }
}