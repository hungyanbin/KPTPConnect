package com.yanbin.ptpsample.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yanbin.ptp.camera.CameraImage
import com.yanbin.ptpsample.R
import com.yanbin.ptpsample.ui.theme.PtpSampleTheme
import java.time.LocalDateTime

@Composable
fun CameraFragment(
    modifier: Modifier = Modifier,
    cameraName: String,
    images: List<CameraImage>
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Camera name: $cameraName",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "Photos:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp),
        ) {
            items(images) { image ->
                CameraImageView(
                    image = image,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                    )
            }
        }
    }
}

@Composable
fun CameraImageView(
    modifier: Modifier = Modifier,
    image: CameraImage
) {
    AsyncImage(
        model = image.thumbUrl,
        contentDescription = image.fileName,
        placeholder = painterResource(id = R.drawable.my_cat),
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun CameraFragmentPreview() {
    val fakeImage = CameraImage(
        id = 1,
        format = "JPEG",
        width = 1920,
        height = 1080,
        fileName = "IMG_20210901_123456.jpg",
        dataCreated = LocalDateTime.now(),
        sourceUrl = "https://example.com/IMG_20210901_123456.jpg",
        thumbUrl = "https://example.com/thumb/IMG_20210901_123456.jpg"
    )
    PtpSampleTheme {
        CameraFragment(
            cameraName = "Sony",
            modifier = Modifier.fillMaxSize(),
            images = listOf(
                fakeImage,fakeImage,fakeImage,fakeImage,fakeImage,fakeImage,fakeImage,
            )
        )
    }
}