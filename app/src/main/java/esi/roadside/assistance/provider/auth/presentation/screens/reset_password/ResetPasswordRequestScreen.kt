package esi.roadside.assistance.provider.auth.presentation.screens.reset_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
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
fun ResetPasswordRequestScreen(
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
                error = uiState.emailError,
                autoCompleteContentType = ContentType.EmailAddress + ContentType.Username
            )
            Spacer(Modifier.height(24.dp))
            Button(stringResource(R.string.send_reset_code), Modifier.fillMaxWidth()) {
                onAction(Action.SendCodeToResetEmail)
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
        ResetPasswordRequestScreen(ResetPasswordUiState(), {})
    }
}

@Preview
@Composable
private fun ResendScreenPreview() {
    PreviewAppTheme {
        ResetPasswordRequestScreen(ResetPasswordUiState(sent = true), {})
    }
}