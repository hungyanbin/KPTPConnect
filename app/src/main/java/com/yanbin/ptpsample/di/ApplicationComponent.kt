package com.yanbin.ptpsample.di

import com.yanbin.ptpsample.MainActivity
import com.yanbin.ptpsample.usb.UsbBroadcastReceiver
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class])
@Singleton
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(usbBroadcastReceiver: UsbBroadcastReceiver)
}