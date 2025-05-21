package esi.roadside.assistance.provider.main.presentation.routes.home

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
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
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.util.composables.CollectEvents
import esi.roadside.assistance.provider.main.domain.repository.ServiceState
import esi.roadside.assistance.provider.main.presentation.Action
import esi.roadside.assistance.provider.main.presentation.components.ServiceListItem
import esi.roadside.assistance.provider.main.presentation.components.TopBar
import esi.roadside.assistance.provider.main.presentation.sheet.NavigatingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isApproved: Boolean,
    uiState: HomeUiState,
    currentService: ServiceState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
    CollectEvents { event ->
        if (event is Event.RemoveRoutes) followLocation(state) {
            it?.let {
                onAction(Action.SetLocation(it))
            }
        }
    }
    LaunchedEffect(uiState.directions) {
        routeLine = uiState.directions?.let { route ->
            Log.i("MainActivity", "Route: ${route.geometry}")
            withContext(Dispatchers.IO) {
                LineString.fromLngLats(route.geometry.coordinates.map {
                    Point.fromLngLat(it[0], it[1])
                }).also {
                    state.transitionToOverviewState(
                        OverviewViewportStateOptions.Builder().geometry(it)
                            .padding(
                                EdgeInsets(0.0, 100.0, 350.0, 100.0)
                            ).build()
                    )
                }
            }
        }
    }
    val sheetState = rememberStandardBottomSheetState(SheetValue.Hidden, skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
    val scope = rememberCoroutineScope()
    LaunchedEffect(currentService.providerState) {
        scope.launch {
            if (currentService.providerState in setOf(ProviderState.NAVIGATING, ProviderState.WORKING))
                sheetState.expand()
            else
                sheetState.hide()
        }
    }
    val marker = rememberIconImage(R.drawable.baseline_location_pin_24)
    BottomSheetScaffold(
        modifier = modifier,
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetContent = {
            if (currentService.providerState in setOf(ProviderState.NAVIGATING, ProviderState.WORKING)) {
                NavigatingScreen(
                    currentService.clientInfo,
                    uiState.message,
                    onAction
                )
            }
        }
    ) {
        Box(Modifier.fillMaxSize().padding(it)) {
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
//                    ScaleBar(
//                        modifier = Modifier.navigationBarsPadding(),
//                        alignment = Alignment.BottomStart
//                    )
                },
                logo = {
                    Logo(
                        alignment = Alignment.TopStart,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                    )
                },
                style = {
                    if (routeLine != null)
                        NavigationStyle(
                            routeLine = routeLine,
                            progress = progress,
                            lightPreset = lightPreset
                        )
                    else
                        MapboxStandardStyle()
                }
            ) {
                currentService.services.forEach {
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
                        it?.let {
                            onAction(Action.SetLocation(it))
                        }
                    }
                }
            }
            TopBar(
                serviceState = currentService,
                modifier = Modifier.align(Alignment.TopStart),
                loading = uiState.loading,
                eta = uiState.directions?.let {
                    (uiState.directions.duration / 60)
                },
                onArrived = {
                    onAction(Action.Arrived)
                },
                onOpenGoogleMaps = {
                    currentService.serviceModel?.serviceLocation?.let {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://www.google.com/maps/dir/?api=1&destination=${it.latitude},${it.longitude}".toUri()
                        )
                        context.startActivity(intent)
                    }
                }
            )
            BottomContent(
                isApproved = isApproved,
                serviceState = currentService,
                loading = uiState.loading,
                modifier = Modifier.align(Alignment.BottomCenter),
                onRefresh = {
                    onAction(Action.RefreshUser)
                },
                onFollowLocation = {
                    followLocation(state) {
                        it?.let {
                            onAction(Action.SetLocation(it))
                        }
                    }
                },
                onIndexChange = {
                    onAction(Action.SelectService(it))
                }
            ) {
                onAction(Action.AcceptService(it))
            }
        }
    }
}

@Composable
fun BottomContent(
    isApproved: Boolean,
    serviceState: ServiceState,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    onFollowLocation: () -> Unit,
    onIndexChange: (Int) -> Unit,
    onAccept: (Int) -> Unit,
) {
    val pagerState = rememberPagerState {
        serviceState.services.size
    }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var isScrolledByUser by remember { mutableStateOf(false) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolledByUser && !isScrolling) {
                onIndexChange(pagerState.settledPage)
            }
            isScrolledByUser = isScrolling && isDragged
        }
    }
    AnimatedVisibility(serviceState.providerState == ProviderState.IDLE, modifier.fillMaxWidth()) {
        Column(
            Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onFollowLocation,
                Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Icon(Icons.Default.MyLocation, null)
            }
            AnimatedVisibility(serviceState.services.isNotEmpty()) {
                HorizontalPager(
                    pagerState,
                    modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    ServiceListItem(serviceState.services[it], loading, { onIndexChange(it) }) {
                        onAccept(it)
                    }
                }
            }
            AnimatedVisibility(!isApproved, Modifier.padding(horizontal = 16.dp)) {
                ElevatedCard(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.not_approved), Modifier.weight(1f))
                        Button(onRefresh, enabled = !loading) {
                            AnimatedContent(loading) {
                                if (it)
                                    CircularProgressIndicator(
                                        Modifier.size(ButtonDefaults.IconSize),
                                        strokeWidth = 2.dp
                                    )
                                else
                                    Icon(
                                        Icons.Default.Refresh,
                                        null,
                                        Modifier.size(ButtonDefaults.IconSize)
                                    )
                            }
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.refresh))
                        }
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
    val color = Color(72, 145, 226)
    val outline = Color(55, 112, 175)
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
//                    lineGradient = ColorValue(
//                        interpolate {
//                            linear()
//                            lineProgress()
//                            stop {
//                                literal(0)
//                                rgba(47.0, 122.0, 198.0, 1.0)
//                            }
//                            stop {
//                                literal(1.0)
//                                rgba(47.0, 122.0, 198.0, 1.0)
//                            }
//                        }
//                    )
                    lineColor = ColorValue(outline)
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
//                    lineGradient = ColorValue(
//                        interpolate {
//                            linear()
//                            lineProgress()
//                            // blue
//                            stop { literal(0.0); rgb { literal(6); literal(1); literal(255) } }
//                            // royal blue
//                            stop { literal(0.1); rgb { literal(59); literal(118); literal(227) } }
//                            // cyan
//                            stop { literal(0.3); rgb { literal(7); literal(238); literal(251) } }
//                            // lime
//                            stop { literal(0.5); rgb { literal(0); literal(255); literal(42) } }
//                            // yellow
//                            stop { literal(0.7); rgb { literal(255); literal(252); literal(0) } }
//                            // red
//                            stop { literal(1.0); rgb { literal(255); literal(30); literal(0) } }
//                        }
//                    )
                    lineColor = ColorValue(color)
                }
            }
        }
    ) {
        this.lightPreset = lightPreset
    }
}
