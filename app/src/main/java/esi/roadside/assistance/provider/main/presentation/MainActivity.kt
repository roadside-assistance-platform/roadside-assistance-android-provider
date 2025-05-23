package esi.roadside.assistance.provider.main.presentation

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.presentation.AuthActivity
import esi.roadside.assistance.provider.core.presentation.components.IconDialog
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.MainNavigate
import esi.roadside.assistance.provider.core.util.composables.CollectEvents
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import esi.roadside.assistance.provider.main.presentation.routes.home.followLocation
import esi.roadside.assistance.provider.main.util.isPermissionGranted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    lateinit var permissionsManager: PermissionsManager
    lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(
                object : PermissionsListener {
                    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                        TODO("Not yet implemented")
                    }

                    override fun onPermissionResult(granted: Boolean) {
                        TODO("Not yet implemented")
                    }
                }
            )
            permissionsManager.requestLocationPermissions(this)
        }
        setContent {
            SetSystemBarColors()
            val navController = rememberNavController()
            mainViewModel = koinViewModel()
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            var isGranted by remember { mutableStateOf<Boolean?>(null) }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                isGranted = it
            }
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            CollectEvents {
                when(it) {
                    is MainNavigate -> navController.navigate(it.route)
                    is Event.ShowMainActivityMessage ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message = getString(it.text))
                        }
                    Event.ExitToAuthActivity -> {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    }
                    Event.RemoveRoutes -> {
                        mainViewModel.onAction(Action.RemoveRoutes)
                    }
                    else -> Unit
                }
            }
            AppTheme {
                NavigationScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    mainViewModel = mainViewModel,
                    onAction = mainViewModel::onAction
                )
                IconDialog(
                    visible = isGranted == false,
                    onDismissRequest = {
                        isGranted = null
                    },
                    title = stringResource(R.string.notification_permission_title),
                    text = stringResource(R.string.notification_permission_text),
                    icon = Icons.Default.NotificationsActive,
                    okListener = {
                        isGranted = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val permission = Manifest.permission.POST_NOTIFICATIONS
                            if (isPermissionGranted(permission)) {
                                NotificationManagerCompat.from(this).areNotificationsEnabled()
                            } else {
                                launcher.launch(permission)
                            }
                        } else {
                            NotificationManagerCompat.from(this).areNotificationsEnabled()
                        }
                    },
                    cancelListener = {
                        isGranted = null
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val serviceId = intent.getStringExtra("serviceId")
        val action = intent.getStringExtra("action")
        serviceId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.serviceState.first().services.indexOfFirst {
                    it.id == serviceId
                }.takeIf { it != -1 }?.let { service ->
                    if (action == "accept") {
                        mainViewModel.onAction(Action.AcceptService(service))
                    } else {
                        mainViewModel.onAction(Action.SelectService(service))
                    }
                }
            }
        }
    }
}