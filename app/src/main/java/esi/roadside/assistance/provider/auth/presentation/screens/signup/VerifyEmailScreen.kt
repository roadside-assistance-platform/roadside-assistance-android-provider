package esi.roadside.assistance.provider.auth.presentation.screens.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.presentation.OtpAction
import esi.roadside.assistance.provider.auth.presentation.util.Button
import esi.roadside.assistance.provider.auth.presentation.util.MyScreen
import esi.roadside.assistance.provider.core.presentation.theme.PreviewAppTheme

@Composable
fun VerifyEmailScreen(
    uiState: SignupUiState,
    onAction: (SignupAction) -> Unit,
    otpState: OtpState,
    onOtpAction: (OtpAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember {
        List(6) { FocusRequester() }
    }

    LaunchedEffect(uiState.loading) {
        if (!uiState.loading)
            focusRequesters[otpState.code.indexOfFirst { it == null }.takeUnless { it == -1 } ?: 5]
                .requestFocus()
    }

    MyScreen(
        stringResource(R.string.verify_email),
        stringResource(R.string.verify_email_text),
        modifier
    ) {
        Column(
            Modifier.padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OtpScreen(
                state = otpState,
                focusRequesters = focusRequesters,
                enabled = !uiState.loading,
                onAction = { action ->
                    when(action) {
                        is OtpAction.OnEnterNumber -> {
                            if(action.number != null) {
                                focusRequesters[action.index].freeFocus()
                            }
                        }
                        else -> Unit
                    }
                    onOtpAction(action)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.didn_t_receive_anything))
                TextButton(
                    {
                        onAction(SignupAction.SendCodeToEmail)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                ) {
                    Text(stringResource(R.string.send_code_again))
                }
            }
            AnimatedContent(uiState.loading) {
                if (it)
                    LinearProgressIndicator(Modifier.padding(vertical = 30.dp).fillMaxWidth())
                else
                    Button(
                        stringResource(R.string.sign_up),
                        Modifier.fillMaxWidth(),
                        enabled = !uiState.loading,
                        onClick = {
                            onAction(SignupAction.Verify)
                        }
                    )
            }
        }
    }
}

@Preview
@Composable
private fun VerifyEmailScreenPreview() {
    PreviewAppTheme {
    }
}