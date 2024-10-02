package com.yanbin.ptpsample.usb

import android.hardware.usb.UsbDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AndroidUsbDeviceRepository @Inject constructor(
    private val ptpFacade: UsbFacade,
    private val coroutineScope: CoroutineScope
): UsbDeviceRepository {

    private var permissionHelper: UsbPermissionHelper? = null
    private val permissionSignal = MutableStateFlow(0)
    private val connectingDeviceIdSignal = MutableStateFlow(-1)

    private val usbDeviceItemsFlow = combine(
        permissionSignal,
        ptpFacade.connectedDevicesFlow(),
        connectingDeviceIdSignal
    ) { _, devices, connectingDeviceId ->
        devices.map {
            UsbDeviceItem(it.deviceId, "${it.manufacturerName} ${it.productName}", ptpFacade.hasPermission(it), it.deviceId == connectingDeviceId)
        }
    }.stateIn(coroutineScope, SharingStarted.Lazily, emptyList())

    override fun getUsbDevices(): Flow<List<UsbDeviceItem>> {
        return usbDeviceItemsFlow
    }

    override fun requestPermission(usbDeviceItem: UsbDeviceItem, listener: UsbPermissionListener) {
        val usbDevice = ptpFacade.getConnectingDevices().first { it.deviceId == usbDeviceItem.deviceId }
        permissionHelper?.requestPermission(usbDevice, object: UsbPermissionListener {
            override fun onPermissionGranted(usbDevice: UsbDevice) {
                listener.onPermissionGranted(usbDevice)
                coroutineScope.launch {
                    permissionSignal.emit(usbDevice.deviceId)
                }
            }

            override fun onPermissionDenied() {
                listener.onPermissionDenied()
            }
        })
    }

    override fun setUsbPermissionHelper(usbPermissionHelper: UsbPermissionHelper) {
        this.permissionHelper = usbPermissionHelper
    }

    override fun setConnectingDevice(deviceId: Int) {
        connectingDeviceIdSignal.update { deviceId }
    }

    override fun clearConnectingDevice() {
        connectingDeviceIdSignal.update { -1 }
    }
}