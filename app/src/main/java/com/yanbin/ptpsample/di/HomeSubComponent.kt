package com.yanbin.ptpsample.di

import com.yanbin.ptpsample.home.HomeViewModel
import com.yanbin.ptpsample.usb.UsbPermissionHelper
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Singleton

@HomeScope
@Subcomponent(modules = [])
interface HomeSubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance permissionHelper: UsbPermissionHelper,
        ): HomeSubComponent
    }

    @HomeScope
    val homeViewModel: HomeViewModel
}