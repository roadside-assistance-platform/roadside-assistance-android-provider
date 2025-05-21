package esi.roadside.assistance.provider.main.presentation.routes.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.twotone.Route
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import esi.roadside.assistance.provider.main.presentation.components.TopAppBar
import esi.roadside.assistance.provider.main.util.plus
import esi.roadside.assistance.provider.settings.presentation.checkSettingsItem
import esi.roadside.assistance.provider.settings.presentation.settingsItem
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsSettingsScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val dataStore = koinInject<SettingsDataStore>()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val maxDistanceFilter by dataStore.maxDistanceFilter.collectAsState(initial = true)
    val maxDistance by dataStore.maxDistance.collectAsState(initial = 100)
    var selectedDistance by remember { mutableFloatStateOf(maxDistance.toFloat()) }
    LaunchedEffect(maxDistance) {
        selectedDistance = maxDistance.toFloat()
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.maps_settings),
                background = R.drawable.union,
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            contentPadding = paddingValues + PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            checkSettingsItem(
                title = R.string.max_distance_filter,
                text = R.string.max_distance_filter_desc,
                icon = Icons.TwoTone.Route,
                checked = maxDistanceFilter,
                onClick = {
                    scope.launch {
                        dataStore.saveSettings(maxDistanceFilter = !maxDistanceFilter)
                    }
                          },
                onCheckedChange = {
                    scope.launch {
                        dataStore.saveSettings(maxDistanceFilter = it)
                    }
                                  },
            )
            settingsItem(
                title = R.string.max_distance,
                text = R.string.max_distance_desc,
                icon = Icons.TwoTone.Route,
                visible = maxDistanceFilter,
                trailingContent = {
                    Text(
                        stringResource(R.string.int_km, selectedDistance.roundToInt()),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
            if (maxDistanceFilter) {
                item {
                    Slider(
                        value = selectedDistance.toFloat(),
                        onValueChange = { newValue ->
                            selectedDistance = newValue
                        },
                        onValueChangeFinished = {
                            scope.launch {
                                dataStore.saveSettings(maxDistance = selectedDistance.roundToInt())
                            }
                        },
                        valueRange = 1f..100f,
                        modifier = Modifier.animateItem().fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}