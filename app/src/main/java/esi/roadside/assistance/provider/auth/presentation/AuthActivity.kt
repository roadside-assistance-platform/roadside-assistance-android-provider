package esi.roadside.assistance.provider.auth.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.presentation.screens.login.LoginScreen
import esi.roadside.assistance.provider.auth.presentation.screens.reset_password.ResetPasswordScreen
import esi.roadside.assistance.provider.auth.presentation.screens.signup.SignupScreen
import esi.roadside.assistance.provider.auth.presentation.screens.signup.SignupSecondScreen
import esi.roadside.assistance.provider.auth.presentation.screens.signup.VerifyEmailScreen
import esi.roadside.assistance.provider.auth.presentation.screens.welcome.WelcomeScreen
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.util.composables.CollectEvents
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import esi.roadside.assistance.provider.main.presentation.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut


class AuthActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinContext {
                SetSystemBarColors()
                val navController = rememberNavController()
                val viewModel: AuthViewModel = koinViewModel()
                val step by viewModel.step.collectAsState()
                val authUiState by viewModel.state.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.onAction(Action.Initiate)
                }
                CollectEvents { event ->
                    when (event) {
                        is Event.AuthNavigate -> {
                            navController.navigate(event.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        is Event.AuthNavigateBackward -> {
                            navController.navigateUp()
                        }
                        is Event.AuthShowError -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(event.error.text))
                            }
                        }
                        is Event.ShowAuthActivityMessage -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(event.text))
                            }
                        }
                        Event.LaunchMainActivity -> {
                            startActivity(Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        }
                        Event.ImageUploadError -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = getString(R.string.error_uploading_image)
                                )
                            }
                        }
                        else -> Unit
                    }
                }
                AppTheme {
                    Scaffold(
                        Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = NavRoutes.Welcome,
                            enterTransition = { materialFadeThroughIn() },
                            exitTransition = { materialFadeThroughOut() },
                            modifier = Modifier.fillMaxSize().padding(it),
                        ) {
                            composable<NavRoutes.Welcome> {
                                WelcomeScreen(step, authUiState.loading, viewModel::onAction)
                            }
                            composable<NavRoutes.Login> {
                                LoginScreen {
                                    navController.navigate(it)
                                }
                            }
                            composable<NavRoutes.Signup> {
                                SignupScreen {
                                    navController.navigate(it)
                                }
                            }
                            composable<NavRoutes.Signup2> {
                                SignupSecondScreen()
                            }
                            composable<NavRoutes.VerifyEmail> {
                                VerifyEmailScreen()
                            }
                            composable<NavRoutes.ForgotPassword> {
                                ResetPasswordScreen {
                                    navController.navigate(it)
                                }
                            }
                        }
                        if (authUiState.errorDialogVisible)
                            ModalBottomSheet(
                                onDismissRequest = {},
                                sheetState = rememberModalBottomSheetState(true) {
                                    it != SheetValue.Hidden
                                },
                                properties = ModalBottomSheetProperties(
                                    securePolicy = SecureFlagPolicy.SecureOn,
                                    shouldDismissOnBackPress = false
                                ),
                                dragHandle = null,
                            ) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    contentPadding = PaddingValues(vertical = 60.dp)
                                ) {
                                    authUiState.error?.let { error ->
                                        error.icon?.let {
                                            item {
                                                Icon(
                                                    imageVector = it,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(100.dp)
                                                )
                                            }
                                        }
                                        item {
                                            Text(
                                                text = stringResource(error.text),
                                                modifier = Modifier.fillMaxWidth(),
                                                style = MaterialTheme.typography.titleLarge,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        error.description?.let {
                                            item {
                                                Text(
                                                    text = stringResource(it),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        authUiState.action?.let {
                                            item {
                                                Button(it.second, enabled = !authUiState.loading) {
                                                    Text(
                                                        text = stringResource(it.first),
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}