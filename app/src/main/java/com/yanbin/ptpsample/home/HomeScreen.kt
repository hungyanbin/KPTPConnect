package com.yanbin.ptpsample.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yanbin.ptpsample.R
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

    HomeScreenContent(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        usbDevices = usbDevices,
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
                .height(200.dp)
        ) {
            items(usbDevices) { usbDevice ->
                DeviceItemView(
                    modifier = Modifier.fillMaxWidth(),
                    device = usbDevice,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }

    }
}

@Composable
fun DeviceItemView(
    modifier: Modifier,
    device: UsbDeviceItem,
    onDeviceSelected: (UsbDeviceItem) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onDeviceSelected(device)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (device.isConnecting) {
                val infiniteTransition = rememberInfiniteTransition(label = "connecting")
                val colorGreen1 = Color(0xFF4CAF50)
                val colorGreen2 = Color(0x884CAF50)
                val color by infiniteTransition.animateColor(
                    initialValue = colorGreen1,
                    targetValue = colorGreen2,
                    animationSpec = infiniteRepeatable(tween(1000), repeatMode = RepeatMode.Reverse),
                    label = "connecting"
                )
                Box(modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color))
            } else {
                Box(modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.Red))
            }
            Text(text = device.name, modifier = Modifier.padding(start = 8.dp))
        }

        val resource = if (device.hasPermission) {
            R.drawable.icon_check
        } else {
            R.drawable.icon_error
        }
        Icon(painter = painterResource(id = resource), contentDescription = "hasPermission", modifier = Modifier.padding(start = 20.dp))
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
            onDeviceSelected = {}
        )
    }
}