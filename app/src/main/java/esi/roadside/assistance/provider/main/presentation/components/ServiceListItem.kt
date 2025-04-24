package esi.roadside.assistance.provider.main.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import esi.roadside.assistance.provider.main.presentation.Action

@Composable
fun ServiceListItem(
    service: ServiceModel,
    selected: Boolean,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    service.clientId,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    service.serviceLocationString,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(selected) {
            Row(
                Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onCancel) {
                    Text(stringResource(R.string.cancel))
                }
                Button(onAccept) {
                    Text(stringResource(R.string.accept))
                }
            }
        }
    }
}