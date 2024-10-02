package com.yanbin.ptpsample.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.yanbin.ptpsample.MainApplication
import timber.log.Timber
import javax.inject.Inject

class UsbBroadcastReceiver: BroadcastReceiver() {

    @Inject lateinit var ptpFacade: UsbFacade

    companion object {
        const val ACTION_USB_PERMISSION = "com.yanbin.ptpsample.USB_PERMISSION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as MainApplication).getComponent().inject(this)
        when(intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED, UsbManager.ACTION_USB_DEVICE_DETACHED, "android.hardware.usb.action.USB_STATE" -> {
                ptpFacade.refreshDeviceList()
            }
            ACTION_USB_PERMISSION -> {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    val granted: Boolean = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    if (granted && device != null) {
                        Timber.i("device: ${device.deviceId} ${device.manufacturerName} ${device.productName}")
                        ptpFacade.onPermissionGranted(device)
                    } else {
                        ptpFacade.onPermissionDenied()
                        Timber.i("permission denied for device $device")
                    }
                }
            }
        }
    }
}