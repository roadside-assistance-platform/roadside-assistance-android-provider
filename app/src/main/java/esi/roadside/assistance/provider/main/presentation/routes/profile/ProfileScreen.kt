package esi.roadside.assistance.provider.main.presentation.routes.profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.components.IconDialog
import esi.roadside.assistance.provider.core.presentation.components.ProfilePicturePicker
import esi.roadside.assistance.provider.core.presentation.theme.PreviewAppTheme
import esi.roadside.assistance.provider.main.presentation.components.DefaultBackNavButton
import esi.roadside.assistance.provider.main.presentation.components.InformationCard
import esi.roadside.assistance.provider.main.presentation.components.TopAppBar
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState(ProfileUiState())
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val focusRequester = remember { FocusRequester() }
    var image by remember { mutableStateOf<Any?>(null) }
    val context = LocalContext.current
    val categories = remember(state.user.categories) {
        state.user.categories.joinToString(", ") { context.getString(it.text) }
    }
    LaunchedEffect(state.enableEditing) {
        if (state.enableEditing) focusRequester.requestFocus()
    }
    LaunchedEffect(state.editUser.photo) {
        if (state.enableEditing) image = state.editUser.photo
    }
    LaunchedEffect(state.photo) {
        if (!state.enableEditing) image = state.photo
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.profile),
                background = R.drawable.vector_9,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    AnimatedVisibility(!state.enableEditing) {
                        DefaultBackNavButton()
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedContent(state.loading and !state.dialog) {
                if (it)
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                else {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                    ) {
                        if (state.enableEditing)
                            FloatingActionButton(
                                { viewModel.onAction(ProfileAction.CancelProfileEditing) },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error,
                            ) {
                                Icon(Icons.Default.Cancel, null)
                            }
                        ExtendedFloatingActionButton(
                            onClick = {
                                viewModel.onAction(
                                    if (state.enableEditing) ProfileAction.ConfirmProfileEditing
                                    else ProfileAction.EnableProfileEditing
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector =
                                        if (state.enableEditing) Icons.Outlined.Check
                                        else Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    if (state.enableEditing) stringResource(R.string.save)
                                    else stringResource(R.string.edit_profile)
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .imePadding()
                .padding(it)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box {
                ProfilePicturePicker(
                    image = image,
                    icon = Icons.Default.Person,
                    enabled = state.enableEditing,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    viewModel.onAction(ProfileAction.EditUser(
                        state.editUser.copy(
                            photo = it
                        )
                    ))
                }
                androidx.compose.animation.AnimatedVisibility(
                    (!state.user.isApproved) and (!state.enableEditing),
                    Modifier.align(Alignment.BottomEnd).offset(x = 16.dp)
                ) {
                    FloatingActionButton(
                        { viewModel.onAction(ProfileAction.ShowDialog) },
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            null
                        )
                    }
                }
            }
            InformationCard(
                icon = Icons.Outlined.Title,
                title = R.string.full_name,
                text = state.user.fullName,
                value = state.editUser.fullName,
                onValueChange = { viewModel.onAction(ProfileAction.EditUser(
                    state.editUser.copy(
                       fullName = it
                    )
                )) },
                enabled = state.enableEditing,
                focusRequester = focusRequester,
                error = state.fullNameError
            )
            InformationCard(
                icon = Icons.Outlined.Email,
                title = R.string.email_adress,
                text = state.user.email,
                value = state.editUser.email,
                onValueChange = { viewModel.onAction(ProfileAction.EditUser(
                    state.editUser.copy(
                        email = it
                    )
                )) },
                enabled = state.enableEditing,
                error = state.emailError
            )
            InformationCard(
                icon = Icons.Outlined.Phone,
                title = R.string.phone_number,
                text = state.user.phone,
                value = state.editUser.phone,
                onValueChange = { viewModel.onAction(ProfileAction.EditUser(
                    state.editUser.copy(
                        phone = it
                    )
                )) },
                enabled = state.enableEditing,
                error = state.phoneError
            )
            InformationCard(
                icon = Icons.Outlined.Category,
                title = R.string.category,
                text = categories,
                value = "",
                onValueChange = {},
                editable = false,
                enabled = state.enableEditing,
                error = state.phoneError
            )
            InformationCard(
                icon = Icons.Outlined.Star,
                title = R.string.rating,
                text = state.user.averageRating?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: stringResource(R.string.not_available),
                value = "",
                onValueChange = {},
                editable = false,
                enabled = state.enableEditing
            )
        }
    }
    BackHandler(enabled = state.enableEditing) {
        viewModel.onAction(ProfileAction.CancelProfileEditing)
    }
    IconDialog(
        visible = state.dialog,
        onDismissRequest = { viewModel.onAction(ProfileAction.HideDialog) },
        icon = Icons.Default.Warning,
        title = stringResource(R.string.warning),
        text = stringResource(R.string.not_approved)
    ) {
        AnimatedContent(state.loading) {
            if (it)
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(30.dp))
            else
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    OutlinedButton(
                        { viewModel.onAction(ProfileAction.HideDialog) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                    Button(
                        { viewModel.onAction(ProfileAction.Refresh) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.refresh))
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PreviewAppTheme {
//        ProfileScreen(
//            state = ProfileUiState(
//                user = ProviderUi(
//                    fullName = "John Doe",
//                    email = "email@example.com",
//                    phone = "0123456789"
//                ),
//                enableEditing = false
//            ),
//            {}
//        )
    }
}