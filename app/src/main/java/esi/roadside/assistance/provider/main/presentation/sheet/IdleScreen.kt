package esi.roadside.assistance.provider.main.presentation.sheet

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import esi.roadside.assistance.provider.main.presentation.Action
import esi.roadside.assistance.provider.main.presentation.components.ServiceListItem
import esi.roadside.assistance.provider.main.presentation.routes.home.followLocation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdleScreen(
    services: List<NotificationServiceModel>,
    selectedService: Int?,
    onAction: (Action) -> Unit,
    mapState: MapViewportState,
    bottomSheetState: SheetState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    AnimatedContent(services.isEmpty(), modifier) {
        if (it)
            Box(Modifier.height(100.dp)) {
                Text(
                    stringResource(R.string.no_nearby_clients),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        else {
            LazyColumn(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                services.forEachIndexed { index, service ->
                    if (selectedService?.let { it == index } != false)
                        item(service.id) {
                            ServiceListItem(
                                service,
                                selectedService?.let { it == index } == true,
                                onCancel = {
                                    onAction(Action.UnSelectService)
                                    scope.launch {
                                        bottomSheetState.partialExpand()
                                    }
                                    followLocation(mapState) {
                                        if (it != null) {
                                            onAction(Action.SetLocation(it))
                                        }
                                    }
                                },
                                onAccept = {
                                    onAction(Action.AcceptService(index))
                                },
                                modifier = Modifier.animateItem()
                            ) {
                                onAction(Action.SelectService(index))
                            }
                        }
                }
            }
        }
    }
}