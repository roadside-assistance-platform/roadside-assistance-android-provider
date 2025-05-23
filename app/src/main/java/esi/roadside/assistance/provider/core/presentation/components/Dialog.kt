package esi.roadside.assistance.provider.core.presentation.components

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.util.composables.isCompact
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    title: String = "",
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    centerTitle: Boolean = false,
    cancelListener: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.cancel),
    okText: String = stringResource(R.string.ok),
    okListener: (() -> Unit)? = onDismissRequest,
    neutral: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit),
) {
    if (visible) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier =
            Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(24.dp),
                )
                .clip(RoundedCornerShape(24.dp))
                .clipToBounds()
                .fillMaxWidth(0.9f)
                .widthIn(min = 280.dp, max = 560.dp),
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = !isCompact,
                ),
        ) {
            Surface(
                contentColor = MaterialTheme.colorScheme.onBackground,
                color = Color.Transparent,
            ) {
                Column(Modifier.fillMaxWidth()) {
                    if (header == null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                textAlign = if (centerTitle) TextAlign.Center else null,
                            )
                            if (trailingContent != null) {
                                trailingContent(this)
                            }
                        }
                    } else {
                        header(this)
                    }
                    content(this)
                    DialogButtons(cancelListener, cancelText, okText, okListener, neutral)
                }
            }
        }
    }
}

@Composable
fun IconDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    icon: ImageVector,
    title: String,
    text: String? = null,
    content: @Composable (ColumnScope.() -> Unit),
) {
    if (visible)
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                        Image(
                            ImageVector.vectorResource(id = R.drawable.material_3_flower),
                            null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                        )
                        Icon(
                            icon,
                            null,
                            Modifier.fillMaxSize(.5f),
                            MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        text?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    content(this)
                }
            }
        }
}