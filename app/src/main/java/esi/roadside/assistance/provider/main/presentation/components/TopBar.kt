package esi.roadside.assistance.provider.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.client.main.util.formatShortTime
import esi.roadside.assistance.client.main.util.formatTime
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.repository.ServiceState
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState
import java.time.LocalDateTime
import kotlin.math.roundToLong

@Composable
fun TopBar(
    serviceState: ServiceState,
    loading: Boolean,
    eta: Double?,
    onArrived: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val etaTime = eta?.let {
        LocalDateTime.now().apply {
            plusSeconds(eta.roundToLong())
        }
    }
    AnimatedVisibility(
        serviceState.providerState in setOf(
            ProviderState.NAVIGATING,
            ProviderState.WORKING,
        ),
        modifier,
        enter = slideInVertically(),
        exit = slideOutVertically { -it }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().statusBarsPadding().padding(20.dp),
                verticalArrangement= Arrangement.spacedBy(16.dp)
            ) {
                AnimatedContent(
                    serviceState.providerState,
                    transitionSpec = {
                        if (initialState > targetState)
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        else
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(it.title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                            val text = when(it) {
                                ProviderState.NAVIGATING ->
                                    "Arrival time: ${etaTime?.formatShortTime() ?: stringResource(R.string.unavailable)}"
                                else -> stringResource(it.text)
                            }
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(2) { index ->
                        var progress =
                            if ((serviceState.providerState.ordinal - 1) >= index) 1f
                            else 0f
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                        )
                        if (serviceState.providerState.ordinal - 1 == index)
                            AnimatedContent(
                                loading,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (it)
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                else
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier.fillMaxWidth(),
                                        drawStopIndicator = {}
                                    )
                            }
                        else
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.weight(1f),
                                drawStopIndicator = {}
                            )
                    }
                }
                AnimatedVisibility(
                    serviceState.providerState == ProviderState.NAVIGATING,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onOpenGoogleMaps,
                            Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                stringResource(R.string.open_in_google_maps),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onArrived,
                            Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Navigation, null, Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                stringResource(R.string.arrived),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}