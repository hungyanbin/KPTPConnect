package com.yanbin.ptpsample.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanbin.ptpsample.usb.UsbDeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val usbDeviceRepository: UsbDeviceRepository
): ViewModel() {
    val usbDevices = usbDeviceRepository.getUsbDevices()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, emptyList())
}