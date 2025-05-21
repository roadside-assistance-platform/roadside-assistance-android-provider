package esi.roadside.assistance.provider.main.presentation.components

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.models.ServiceInfo
import kotlin.math.roundToInt

@Composable
fun ServiceListItem(
    service: ServiceInfo,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAccept: () -> Unit,
) {
    val context = LocalContext.current
    val duration =
        service.directions.routes.minByOrNull { it.duration }?.duration?.let {
            (it / 60).roundToInt()
        }
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Column(
            modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MyImage(
                    service.client.photo,
                    Icons.Default.Person,
                    Modifier.size(60.dp),
                    shape = CircleShape
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        service.client.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    duration?.let { "$duration mins away" }
                                        ?: stringResource(R.string.unavailable),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DriveEta,
                                    null,
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    stringResource(service.category.text),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    ImageVector.vectorResource(service.category.icon),
                                    null,
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    }
                }
                IconButton({
                     Intent(Intent.ACTION_DIAL).apply {
                         data = "tel:${service.client.phone}".toUri()
                     }.let {
                         if (it.resolveActivity(context.packageManager) != null)
                            context.startActivity(it)
                     }
                },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )) {
                    Icon(Icons.Default.Phone, null)
                }
            }
            Column(Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                mapOf(
                    Icons.AutoMirrored.Filled.Comment to service.description,
                    Icons.Default.Email to service.client.email,
                    Icons.Default.Map to service.directions.waypoints.joinToString("\n") {
                        it.name.takeIf { it.isNotBlank() }  ?: "Unknown waypoint"
                    },
                ).forEach { (icon, text) ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            icon,
                            null,
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AnimatedContent(loading, Modifier.fillMaxWidth()) {
                    if (it)
                        LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 8.dp))
                    else
                        Button(onAccept, Modifier.weight(1f)) {
                            Text(stringResource(R.string.accept))
                        }
                }
            }
        }
    }
}

