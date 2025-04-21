package esi.roadside.assistance.provider.main.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import esi.roadside.assistance.provider.core.presentation.util.EventBus
import esi.roadside.assistance.provider.main.domain.models.NotificationModel

@Composable
fun CollectNotifications(
    callback: (NotificationModel) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(key1 = lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            NotificationListener.notifications.collect(callback)
        }
    }
}