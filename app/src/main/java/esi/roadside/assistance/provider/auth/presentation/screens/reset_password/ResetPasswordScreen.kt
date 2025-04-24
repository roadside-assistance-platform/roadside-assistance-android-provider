package esi.roadside.assistance.provider.auth.presentation.screens.reset_password

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.presentation.Action
import esi.roadside.assistance.provider.auth.presentation.util.Button
import esi.roadside.assistance.provider.auth.presentation.util.MyScreen
import esi.roadside.assistance.provider.core.presentation.components.MyTextField
import esi.roadside.assistance.provider.core.presentation.theme.PreviewAppTheme

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordUiState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    MyScreen(
        stringResource(R.string.receive_code),
        stringResource(R.string.send_code_text),
        modifier
    ) {
        Column(
            Modifier.padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MyTextField(
                uiState.email,
                {
                    onAction(Action.SetResetPasswordEmail(it))
                },
                Modifier.fillMaxWidth(),
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.email_placeholder),
                error = uiState.emailError
            )
            Spacer(Modifier.height(24.dp))
            AnimatedVisibility(uiState.sent) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.didn_t_receive_anything))
                    TextButton(
                        { onAction(Action.Send) },
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ),
                    ) {
                        Text(stringResource(R.string.send_code_again))
                    }
                }
            }
            Button(stringResource(R.string.send_reset_code), Modifier.fillMaxWidth()) {
                onAction(Action.Send)
            }
            Spacer(Modifier.height(40.dp))
            TextButton(
                { onAction(Action.Back) },
                contentPadding = PaddingValues(horizontal = 8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
            ) {
                Text(stringResource(R.string.back_to_login))
            }
        }
    }
}

@Preview
@Composable
private fun ResetPasswordScreenPreview() {
    PreviewAppTheme {
        ResetPasswordScreen(ResetPasswordUiState(), {})
    }
}

@Preview
@Composable
private fun ResendScreenPreview() {
    PreviewAppTheme {
        ResetPasswordScreen(ResetPasswordUiState(sent = true), {})
    }
}