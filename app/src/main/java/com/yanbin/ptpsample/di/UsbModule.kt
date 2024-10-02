package com.yanbin.ptpsample.di

import com.yanbin.ptpsample.usb.AndroidUsbDeviceRepository
import com.yanbin.ptpsample.usb.UsbDeviceRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface UsbModule {
    @Binds
    @Singleton
    fun bindUsbDeviceRepository(impl: AndroidUsbDeviceRepository): UsbDeviceRepository

}