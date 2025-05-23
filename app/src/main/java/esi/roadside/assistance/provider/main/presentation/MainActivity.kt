package esi.roadside.assistance.provider.main.presentation

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import esi.roadside.assistance.provider.auth.presentation.AuthActivity
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.MainNavigate
import esi.roadside.assistance.provider.core.util.composables.CollectEvents
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import esi.roadside.assistance.provider.main.util.isPermissionGranted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    lateinit var mainViewModel: MainViewModel
    val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        else
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetSystemBarColors()
            val navController = rememberNavController()
            mainViewModel = koinViewModel()
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            var isGranted by remember { mutableStateOf<Map<String, Boolean?>>(
                mapOf()
            ) }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { granted ->
                isGranted = granted
                if (granted[Manifest.permission.POST_NOTIFICATIONS] == true) {
                    NotificationManagerCompat.from(this).areNotificationsEnabled()
                }
            }
            LaunchedEffect(Unit) {
                isGranted = permissions.associateWith {
                    isPermissionGranted(it).takeIf { it == true }
                }
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
                PermissionsDialog(
                    isGranted = isGranted,
                    refresh = {
                        isGranted = permissions.associateWith {
                            isPermissionGranted(it)
                        }
                    }
                ) {
                    val denied = permissions.filter { isGranted[it] == false }
                    if (denied.isNotEmpty()) {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = android.net.Uri.fromParts("package", packageName, null)
                        }
                        startActivity(intent)
                    } else {
                        launcher.launch(permissions)
                    }
                }
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