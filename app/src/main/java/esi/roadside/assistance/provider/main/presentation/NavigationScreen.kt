package esi.roadside.assistance.provider.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.components.IconDialog
import esi.roadside.assistance.provider.core.util.intUpDownTransSpec
import esi.roadside.assistance.provider.main.presentation.components.RatingBar
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeScreen
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeUiState
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState
import esi.roadside.assistance.provider.main.presentation.routes.notifications.NotificationDetails
import esi.roadside.assistance.provider.main.presentation.routes.notifications.NotificationsScreen
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.AboutScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.ChangePasswordScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.CustomizeAppScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.HelpScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.LanguageScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.MapsSettingsScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.PrivacyPolicyScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.SettingsScreen
import esi.roadside.assistance.provider.main.presentation.routes.settings.TermsOfServiceScreen
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onAction: (Action) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isApproved by mainViewModel.isApproved.collectAsState(true)
    val currentRoute = navBackStackEntry?.destination?.route
    val currentNavRoute =
        currentRoute?.let { route ->
            Routes
                .entries
                .firstOrNull {
                    it.route.javaClass.kotlin.qualifiedName?.contains(route) == true
                }
        } ?: Routes.HOME
    val isParent =
        currentRoute?.let { route ->
            Routes
                .entries
                .any {
                    it.route.javaClass.kotlin.qualifiedName?.contains(route) == true
                }
        } != false
    val homeUiState by mainViewModel.homeUiState.collectAsState(HomeUiState())
    val userNotification by mainViewModel.userNotification.collectAsState()
    val navigationBarVisible = isParent //and (currentNavRoute != Routes.PROFILE)
    val currentService by mainViewModel.serviceState.collectAsState()

    Column(modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.weight(1f),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home,
                modifier = Modifier.fillMaxSize().padding(it),
            ) {
                navigation<NavRoutes.Home>(NavRoutes.Map) {
                    composable<NavRoutes.Map> {
                        HomeScreen(
                            isApproved,
                            homeUiState,
                            currentService,
                            onAction
                        )
                    }
                }
                navigation<NavRoutes.Notifications>(NavRoutes.NotificationsList) {
                    composable<NavRoutes.NotificationsList> {
                        NotificationsScreen(userNotification) {
                            navController.navigate(NavRoutes.Notification(it.id))
                        }
                    }
                    composable<NavRoutes.Notification> { args ->
                        val notification = userNotification.firstOrNull { it.id == args.id }
                        notification?.let { notification ->
                            NotificationDetails(notification)
                        }
                    }
                }
                composable<NavRoutes.Profile> {
                    ProfileScreen()
                }
                navigation<NavRoutes.Settings>(NavRoutes.SettingsList) {
                    composable<NavRoutes.SettingsList> {
                        SettingsScreen(navController, mainViewModel::onAction)
                    }
                    composable<NavRoutes.ChangePassword> {
                         ChangePasswordScreen()
                    }
                    composable<NavRoutes.DeleteAccount> {
                         //DeleteAccountScreen()
                    }
                    composable<NavRoutes.CustomizeApp> {
                        CustomizeAppScreen()
                    }
                    composable<NavRoutes.MapsSettings> {
                        MapsSettingsScreen()
                    }
                    composable<NavRoutes.Language> {
                        LanguageScreen()
                    }
                    composable<NavRoutes.About> {
                        AboutScreen()
                    }
                    composable<NavRoutes.TermsOfService> {
                        TermsOfServiceScreen()
                    }
                    composable<NavRoutes.PrivacyPolicy> {
                        PrivacyPolicyScreen()
                    }
                    composable<NavRoutes.Help> {
                        HelpScreen()
                    }
                }
            }
        }
        AnimatedVisibility(navigationBarVisible) {
            NavigationBar(navController, currentNavRoute) {
                androidx.compose.animation.AnimatedVisibility(
                    (it == Routes.NOTIFICATIONS) and userNotification.isNotEmpty(),
                    enter = materialFadeThroughIn(),
                    exit = materialFadeThroughOut()
                ) {
                    Badge {
                        AnimatedContent(
                            userNotification.size,
                            label = "",
                            transitionSpec = intUpDownTransSpec
                        ) {
                            Text("$it")
                        }
                    }
                }
            }
        }
    }
    IconDialog(
        icon = Icons.Default.Check,
        title = stringResource(R.string.you_earned_dzd, currentService.price),
        text = stringResource(R.string.thank_you_for_your_service),
        visible = currentService.providerState == ProviderState.COMPLETED,
        onDismissRequest = {
            mainViewModel.onAction(Action.HideFinishDialog)
        },
    ) {
        currentService.rating?.let {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                RatingBar(
                    rating = it,
                    modifier = Modifier.fillMaxWidth(.7f),
                    starsColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        Button({ mainViewModel.onAction(Action.HideFinishDialog) }, Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.ok))
        }
    }
}