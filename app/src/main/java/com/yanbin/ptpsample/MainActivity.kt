package com.yanbin.ptpsample

import android.hardware.usb.UsbDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yanbin.ptpsample.di.ApplicationComponent
import com.yanbin.ptpsample.home.HomeScreen
import com.yanbin.ptpsample.ui.theme.PtpSampleTheme
import com.yanbin.ptpsample.usb.UsbFacade
import com.yanbin.ptpsample.usb.UsbPermissionHelper
import com.yanbin.ptpsample.usb.UsbPermissionListener
import javax.inject.Inject

class MainActivity : ComponentActivity(), UsbPermissionHelper {

    private lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var usbFacade: UsbFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationComponent = (application as MainApplication).getComponent()
        applicationComponent.inject(this)

        enableEdgeToEdge()
        setContent {
            PtpSampleTheme {
                val viewModel = applicationComponent.homeViewModel
                HomeScreen(viewModel = viewModel)
            }
        }
    }

    override fun requestPermission(usbDevice: UsbDevice, listener: UsbPermissionListener) {
        usbFacade.requestUsbPermission(this, usbDevice, listener)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PtpSampleTheme {
        Greeting("Android")
    }
}