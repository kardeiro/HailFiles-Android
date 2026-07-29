package com.kardeiro.hailfiles.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HailFilesToast(
    message: String,
    onDismiss: () -> Unit,
    durationMs: Long = 3000L
) {
    LaunchedEffect(message) {
        delay(durationMs)
        onDismiss()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.inverseSurface,
            shadowElevation = 6.dp
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}
