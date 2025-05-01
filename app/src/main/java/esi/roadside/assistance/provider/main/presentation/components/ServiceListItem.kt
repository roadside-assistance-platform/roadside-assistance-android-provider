package esi.roadside.assistance.provider.main.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.ClientInfo
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import androidx.core.net.toUri

@Composable
fun ServiceListItem(
    service: NotificationServiceModel,
    selected: Boolean,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val containerColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainer
    )
    val duration =
        service.directions.routes.minByOrNull { it.duration }?.duration?.let {
            (it / 60).roundToInt()
        }
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Column(
            modifier.clickable(onClick = onClick).padding(16.dp),
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
            AnimatedVisibility(selected) {
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
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onCancel, Modifier.weight(1f)) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(onAccept, Modifier.weight(1f)) {
                            Text(stringResource(R.string.accept))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServiceListItemPreview() {
    AppTheme {
        val model = NotificationServiceModel(
            id = "id1030",
            client = ClientInfo(
                id = "id209",
                fullName = "Younes Bouhouche",
                email = "email@gmail.com",
                phone = "030490495",
                photo = "example.com",
                createdAt = "",
                updatedAt = ""
            ),
            providerId = null,
            price = 2000,
            description = "Description",
            serviceRating = 0f,
            serviceLocation = LocationModel(0.0, 0.0),
            serviceLocationString = "",
            done = false,
            category = Categories.TOWING,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            comments = emptyList()
        )
        Column {
            ServiceListItem(
                service = model,
                selected = false,
                onAccept = {},
                onCancel = {},
                modifier = Modifier,
                onClick = { }
            )
            ServiceListItem(
                service = model,
                selected = true,
                onAccept = {},
                onCancel = {},
                modifier = Modifier,
                onClick = { }
            )
        }
    }
}