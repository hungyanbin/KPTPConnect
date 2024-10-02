package com.yanbin.ptpsample.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleDialog(
    title: String,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(8.dp),
        title = {
            Text(text = title)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "確認")
            }
        },
    )
}