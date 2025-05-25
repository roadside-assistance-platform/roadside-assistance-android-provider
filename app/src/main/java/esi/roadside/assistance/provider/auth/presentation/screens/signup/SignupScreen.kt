package esi.roadside.assistance.provider.auth.presentation.screens.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.presentation.NavRoutes
import esi.roadside.assistance.provider.auth.presentation.util.BackgroundBox
import esi.roadside.assistance.provider.auth.presentation.util.Button
import esi.roadside.assistance.provider.core.presentation.components.MyTextField
import esi.roadside.assistance.provider.core.presentation.components.ProfilePicturePicker
import esi.roadside.assistance.provider.core.presentation.theme.PreviewAppTheme
import esi.roadside.assistance.provider.core.presentation.theme.lightScheme
import esi.roadside.assistance.provider.main.domain.Categories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    uiState: SignupUiState,
    onAction: (SignupAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigate: (NavRoutes) -> Unit
) {

    BackgroundBox(R.drawable.signup_background, modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 48.dp)
                .padding(top = 12.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.join_us),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = lightScheme.background
                )
                Text(
                    text = stringResource(R.string.create_an_account),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = lightScheme.background
                )
                Spacer(Modifier.height(18.dp))
                ProfilePicturePicker(uiState.image, enabled = !uiState.loading) {
                    onAction(SignupAction.SetImage(it))
                }
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyTextField(
                    uiState.fullName,
                    {
                        onAction(SignupAction.SetFullName(it))
                    },
                    label = stringResource(R.string.full_name),
                    placeholder = stringResource(R.string.full_name_placeholder),
                    enabled = !uiState.loading,
                    error = uiState.fullNameError,
                    autoCompleteContentType = ContentType.PersonFullName,
                )
                MyTextField(
                    uiState.phoneNumber,
                    {
                        onAction(SignupAction.SetPhoneNumber(it))
                    },
                    label = stringResource(R.string.phone_number),
                    placeholder = stringResource(R.string.phone_number_placeholder),
                    error = uiState.phoneNumberError,
                    enabled = !uiState.loading,
                    autoCompleteContentType = ContentType.PhoneNumberDevice,
                    keyboardType = KeyboardType.Phone
                )
                Categories.entries.dropLast(1).chunked(2).forEach {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        it.forEach { category ->
                            Row(
                                Modifier
                                    .weight(.5f)
                                    .clip(MaterialTheme.shapes.small)
                                    .padding(end = 2.dp)
                                    .clickable {
                                    onAction(
                                        if (uiState.categories.contains(category))
                                            SignupAction.RemoveCategory(category)
                                        else SignupAction.AddCategory(category)
                                    )
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Checkbox(
                                    checked = uiState.categories.contains(category),
                                    onCheckedChange = {
                                        onAction(
                                            if (it) SignupAction.AddCategory(category)
                                            else SignupAction.RemoveCategory(category)
                                        )
                                    }
                                )
                                Text(
                                    stringResource(category.text),
                                    Modifier.weight(1f),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Button(
                    stringResource(R.string.continue_text),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = !uiState.loading
                ) {
                    onNavigate(NavRoutes.Signup2)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.already_have_an_account))
                    TextButton(
                        { onNavigate(NavRoutes.Login) },
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ),
                        enabled = !uiState.loading,
                    ) {
                        Text(stringResource(R.string.log_in))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SignupScreenPreview() {
    PreviewAppTheme {
        //SignupScreen(SignupUiState(), {})
    }
}