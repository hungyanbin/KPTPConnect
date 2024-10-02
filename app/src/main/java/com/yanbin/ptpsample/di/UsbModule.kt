package com.yanbin.ptpsample.di

import com.yanbin.ptpsample.home.usecase.PtpUsecase
import com.yanbin.ptpsample.home.usecase.PtpUsecaseImpl
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

    @Binds
    @Singleton
    fun bindPtpUsecase(impl: PtpUsecaseImpl): PtpUsecase
}