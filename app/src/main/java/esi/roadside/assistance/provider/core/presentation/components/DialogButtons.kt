package esi.roadside.assistance.provider.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R

@Composable
fun DialogButtons(
    cancelListener: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.cancel),
    okText: String = stringResource(R.string.ok),
    okListener: (() -> Unit)? = null,
    neutral: (@Composable () -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
        val buttons = @Composable {
            if (neutral != null) {
                neutral()
                Spacer(Modifier.width(12.dp))
            }
            if (cancelListener != null) {
                OutlinedButton(
                    onClick = cancelListener,
                    modifier = modifier,
                ) {
                    Text(cancelText)
                }
            }
            if ((okListener != null) and (cancelListener != null)) Spacer(Modifier.width(12.dp))
            if (okListener != null) {
                Button(
                    onClick = okListener,
                    modifier = modifier,
                ) {
                    Text(okText)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            buttons()
        }
    }
}
