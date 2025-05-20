package esi.roadside.assistance.provider.main.presentation.sheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.models.ServiceInfo
import esi.roadside.assistance.provider.main.presentation.Action
import esi.roadside.assistance.provider.main.presentation.components.ServiceListItem
import esi.roadside.assistance.provider.main.presentation.routes.home.followLocation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdleScreen(
    services: List<ServiceInfo>,
    selectedService: Int?,
    loading: Boolean,
    onAction: (Action) -> Unit,
    mapState: MapViewportState,
    bottomSheetState: SheetState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    AnimatedContent(services.isEmpty(), modifier) {
        if (it)
            Text(
                stringResource(R.string.no_nearby_clients),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp).padding(bottom = 24.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
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
                                modifier = Modifier.animateItem(),
                                loading = loading
                            ) {
                                onAction(Action.SelectService(index))
                            }
                        }
                }
            }
        }
    }
}