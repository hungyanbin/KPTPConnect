package com.yanbin.ptpsample.di

import com.yanbin.ptpsample.MainActivity
import com.yanbin.ptpsample.home.HomeViewModel
import com.yanbin.ptpsample.usb.UsbBroadcastReceiver
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class, UsbModule::class])
@Singleton
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(usbBroadcastReceiver: UsbBroadcastReceiver)

    @Singleton
    val homeViewModel: HomeViewModel
}