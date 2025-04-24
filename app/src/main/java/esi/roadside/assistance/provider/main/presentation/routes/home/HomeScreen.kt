package esi.roadside.assistance.provider.main.presentation.routes.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.DoubleListValue
import com.mapbox.maps.extension.compose.style.DoubleValue
import com.mapbox.maps.extension.compose.style.MapboxStyleComposable
import com.mapbox.maps.extension.compose.style.layers.generated.LineCapValue
import com.mapbox.maps.extension.compose.style.layers.generated.LineJoinValue
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.presentation.Action
import esi.roadside.assistance.provider.main.presentation.components.ServiceListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }
    var progress by remember {
        mutableDoubleStateOf(0.0)
    }
    var routeLine by remember {
        mutableStateOf<LineString?>(null)
    }

    var lightPreset by remember {
        mutableStateOf(LightPresetValue.DAY)
    }

//    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
//            routeLine = LineString.fromPolyline(
//                DirectionsResponse.fromJson(
//                    AnnotationUtils.loadStringFromAssets(
//                        context,
//                        "navigation_route.json"
//                    )
//                ).routes()[0].geometry()!!,
//                6
//            ).also {
//                // immediately transition to overview viewport state after route line is available
//                state.transitionToOverviewState(
//                    OverviewViewportStateOptions.Builder().geometry(it)
//                        .padding(EdgeInsets(50.0, 50.0, 50.0, 50.0))
//                        .build()
//                )
//            }
        }
    }
    val marker = rememberIconImage(R.drawable.baseline_location_pin_24)
    val bottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContent = {
            Text(
                stringResource(R.string.nearby_clients),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
            AnimatedContent(uiState.services) {
                if (it.isEmpty())
                    Box(Modifier.height(100.dp)) {
                        Text(
                            stringResource(R.string.no_nearby_clients),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        )
                    }
                else {
                    LazyColumn(Modifier.fillMaxWidth()) {
                        uiState.services.forEachIndexed { index, service ->
                            if (uiState.selectedService?.let { it == index } != false)
                                item(index) {
                                    ServiceListItem(
                                        service,
                                        uiState.selectedService?.let { it == index } == true,
                                        onCancel = {
                                            onAction(Action.UnSelectService)
                                        },
                                        onAccept = {
                                            onAction(Action.AcceptService(index))
                                        },
                                        modifier = Modifier.animateItem()
                                    ) {
                                        scope.launch {
                                            bottomSheetState.partialExpand()
                                        }
                                        onAction(Action.SelectService(index))
                                        state.easeTo(
                                            CameraOptions
                                                .Builder()
                                                .zoom(state.cameraState?.zoom ?: 2.0)
                                                .center(service.serviceLocation.toPoint())
                                                .pitch(0.0)
                                                .bearing(0.0)
                                                .build()
                                        )
                                    }
                                }
                        }
                    }
                }
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 120.dp,
        modifier = modifier,
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    lightPreset = if (lightPreset == LightPresetValue.DAY) {
//                        LightPresetValue.NIGHT
//                    } else {
//                        LightPresetValue.DAY
//                    }
//                },
//            ) {
//                Text(text = "Toggle light preset")
//            }
//        }
    ) {
        Box(Modifier.fillMaxSize()) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = state,
                compass = {
                    Compass(
                        modifier = Modifier.statusBarsPadding(),
                        alignment = Alignment.TopEnd
                    )
                },
                scaleBar = {
                    ScaleBar(
                        modifier = Modifier.navigationBarsPadding(),
                        alignment = Alignment.BottomStart
                    )
                },
                logo = {
                    Logo(alignment = Alignment.TopStart)
                },
                style = {
                    if (uiState.providerState == ProviderState.NAVIGATING)
                        NavigationStyle(
                            routeLine = routeLine,
                            progress = progress,
                            lightPreset = lightPreset
                        )
                    else
                        MapboxStandardStyle()
                }
            ) {
                uiState.services.forEach {
                    PointAnnotation(
                        point = it.serviceLocation
                            .let { Point.fromLngLat(it.longitude, it.latitude) }
                    ) {
                        interactionsState.onClicked {
                            onAction(Action.SetLocation(it.point))
                            state.easeTo(
                                CameraOptions
                                    .Builder()
                                    .zoom(state.cameraState?.zoom ?: 2.0)
                                    .center(it.point)
                                    .pitch(0.0)
                                    .bearing(0.0)
                                    .build()
                            )
                            true
                        }
                        iconImage = marker
                        iconAnchor = IconAnchor.BOTTOM
                        iconImageCrossFade = 1.0
                    }
                }
                MapEffect(Unit) { mapView ->
                    followLocation(state, mapView) {
                        if (it != null) onAction(Action.SetLocation(it))
                    }
                }
            }
        }
    }
}

fun followLocation(
    state: MapViewportState,
    mapView: MapView? = null,
    listener: (Point?) -> Unit = {}
) {
    mapView?.location?.updateSettings {
        locationPuck = createDefault2DPuck(withBearing = false)
        enabled = true
        puckBearing = PuckBearing.COURSE
        puckBearingEnabled = false
    }
    state.transitionToFollowPuckState(
        followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
            .pitch(0.0)
            .bearing(null)
            .build()
    ) {
        if (it) listener(state.cameraState?.center)
    }
}


@MapboxStyleComposable
@Composable
fun NavigationStyle(
    routeLine: LineString?,
    progress: Double,
    lightPreset: LightPresetValue
) {
    val geoJsonSource = rememberGeoJsonSourceState {
        lineMetrics = BooleanValue(true)
    }
    LaunchedEffect(routeLine) {
        routeLine?.let {
            geoJsonSource.data = GeoJSONData(it)
        }
    }
    MapboxStandardStyle(
        topSlot = {
            if (routeLine != null) {
                LineLayer(
                    sourceState = geoJsonSource
                ) {
                    lineTrimOffset = DoubleListValue(listOf(0.0, progress))
                    lineWidth = DoubleValue(
                        interpolate {
                            exponential {
                                literal(1.5)
                            }
                            zoom()
                            stop {
                                literal(10)
                                product(7.0, 1.0)
                            }
                            stop {
                                literal(14.0)
                                product(10.5, 1.0)
                            }
                            stop {
                                literal(16.5)
                                product(15.5, 1.0)
                            }
                            stop {
                                literal(19.0)
                                product(24.0, 1.0)
                            }
                            stop {
                                literal(22.0)
                                product(29.0, 1.0)
                            }
                        }
                    )
                    lineCap = LineCapValue.ROUND
                    lineJoin = LineJoinValue.ROUND
                    lineGradient = ColorValue(
                        interpolate {
                            linear()
                            lineProgress()
                            stop {
                                literal(0)
                                rgba(47.0, 122.0, 198.0, 1.0)
                            }
                            stop {
                                literal(1.0)
                                rgba(47.0, 122.0, 198.0, 1.0)
                            }
                        }
                    )
                }
                LineLayer(
                    sourceState = geoJsonSource
                ) {
                    lineTrimOffset = DoubleListValue(listOf(0.0, progress))
                    lineWidth = DoubleValue(
                        interpolate {
                            exponential {
                                literal(1.5)
                            }
                            zoom()
                            stop {
                                literal(4.0)
                                product(3.0, 1.0)
                            }
                            stop {
                                literal(10.0)
                                product(4.0, 1.0)
                            }
                            stop {
                                literal(13.0)
                                product(6.0, 1.0)
                            }
                            stop {
                                literal(16.0)
                                product(10.0, 1.0)
                            }
                            stop {
                                literal(19.0)
                                product(14.0, 1.0)
                            }
                            stop {
                                literal(22.0)
                                product(18.0, 1.0)
                            }
                        }
                    )
                    lineCap = LineCapValue.ROUND
                    lineJoin = LineJoinValue.ROUND
                    lineGradient = ColorValue(
                        interpolate {
                            linear()
                            lineProgress()
                            // blue
                            stop { literal(0.0); rgb { literal(6); literal(1); literal(255) } }
                            // royal blue
                            stop { literal(0.1); rgb { literal(59); literal(118); literal(227) } }
                            // cyan
                            stop { literal(0.3); rgb { literal(7); literal(238); literal(251) } }
                            // lime
                            stop { literal(0.5); rgb { literal(0); literal(255); literal(42) } }
                            // yellow
                            stop { literal(0.7); rgb { literal(255); literal(252); literal(0) } }
                            // red
                            stop { literal(1.0); rgb { literal(255); literal(30); literal(0) } }
                        }
                    )
                }
            }
        }
    ) {
        this.lightPreset = lightPreset
    }
}
