package esi.roadside.assistance.provider.core.presentation.components

import android.app.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.util.composables.isCompact

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
    title: String = "",
    text: String,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    cancelListener: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.cancel),
    okListener: (() -> Unit)? = onDismissRequest,
    neutral: @Composable (() -> Unit)? = null,
) = Dialog(
    visible = visible,
    onDismissRequest = onDismissRequest,
    header = {
        Column(
            Modifier.padding(top = 24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    },
    trailingContent = trailingContent,
    cancelListener = cancelListener,
    cancelText = cancelText,
    okListener = okListener,
    neutral = neutral,
    content = {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(24.dp),
        )
    }
)