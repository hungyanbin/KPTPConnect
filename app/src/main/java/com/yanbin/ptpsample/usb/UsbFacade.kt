package com.yanbin.ptpsample.usb

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbFacade @Inject constructor(
    private val context: Context
) {

    private var permissionListener: UsbPermissionListener? = null
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private val connectedDevices = MutableStateFlow(emptyList<UsbDevice>())

    init {
        refreshDeviceList()
    }

    fun connectedDevicesFlow(): Flow<List<UsbDevice>> {
        return connectedDevices
    }

    fun getConnectingDevices(): List<UsbDevice> {
        return connectedDevices.value
    }

    fun hasPermission(usbDevice: UsbDevice): Boolean {
        return usbManager.hasPermission(usbDevice)
    }

    fun requestUsbPermission(activity: Activity, usbDevice: UsbDevice, listener: UsbPermissionListener) {
        this.permissionListener = listener
        val intent = Intent(activity, UsbBroadcastReceiver::class.java).apply {
            action = UsbBroadcastReceiver.ACTION_USB_PERMISSION
        }
        val permissionIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_USB, intent, PendingIntent.FLAG_MUTABLE)

        usbManager.requestPermission(usbDevice, permissionIntent)
    }

    fun onPermissionGranted(useDevice: UsbDevice) {
        permissionListener?.onPermissionGranted(useDevice)
    }

    fun onPermissionDenied() {
        permissionListener?.onPermissionDenied()
    }

    fun refreshDeviceList() {
        val devices = usbManager.deviceList.values.toList()
        Timber.d("refreshDeviceList: ${devices.size}")
        connectedDevices.value = devices
    }

    companion object {
        const val REQUEST_CODE_USB = 0
    }
}