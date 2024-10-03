package com.yanbin.ptpsample.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yanbin.ptp.camera.CameraImage
import com.yanbin.ptpsample.ui.theme.PtpSampleTheme
import com.yanbin.ptpsample.usb.UsbDeviceItem
import com.yanbin.ptpsample.view.SimpleDialog

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val usbDevices by viewModel.usbDevices.collectAsState()
    val showSimpleDialog by viewModel.showSimpleDialog.collectAsState()
    val cameraName by viewModel.cameraName.collectAsState()
    val images by viewModel.images.collectAsState()

    HomeScreenContent(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        usbDevices = usbDevices,
        cameraName = cameraName,
        images = images,
        onDeviceSelected = viewModel::onDeviceSelected
    )

    if (showSimpleDialog != null) {
        val dialogTitle = showSimpleDialog ?: ""
        SimpleDialog(
            title = dialogTitle,
            onDismissRequest = viewModel::onSimpleDialogDismissClicked
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    usbDevices: List<UsbDeviceItem>,
    cameraName: String,
    images: List<CameraImage>,
    onDeviceSelected: (UsbDeviceItem) -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Devices:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Gray)
        ) {
            items(usbDevices) { usbDevice ->
                DeviceItemView(
                    modifier = Modifier.fillMaxWidth(),
                    device = usbDevice,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }

        if (cameraName.isNotEmpty()) {
            CameraFragment(
                modifier = Modifier.fillMaxSize(),
                cameraName = cameraName,
                images = images
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PtpSampleTheme {
        HomeScreenContent(
            modifier = Modifier.fillMaxSize(),
            usbDevices = listOf(
                UsbDeviceItem(1, "Device 1", true, true),
                UsbDeviceItem(2, "Device 2", false, false),
                UsbDeviceItem(3, "Device 3", true, false),
                UsbDeviceItem(4, "Device 4", false, true),
            ),
            cameraName = "Sony",
            images = emptyList(),
            onDeviceSelected = {}
        )
    }
}