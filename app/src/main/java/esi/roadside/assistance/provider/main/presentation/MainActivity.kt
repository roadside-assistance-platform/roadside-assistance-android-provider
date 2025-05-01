package esi.roadside.assistance.provider.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import esi.roadside.assistance.provider.auth.presentation.AuthActivity
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.MainNavigate
import esi.roadside.assistance.provider.core.util.composables.CollectEvents
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    lateinit var permissionsManager: PermissionsManager

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
            val mainViewModel : MainViewModel = koinViewModel()
            val bottomSheetState = rememberModalBottomSheetState(true)
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            CollectEvents {
                when(it) {
                    is MainNavigate -> navController.navigate(it.route)
                    is Event.ShowMainActivityMessage ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message = getString(it.text))
                        }
                    Event.ShowRequestAssistance -> scope.launch {
                        bottomSheetState.show()
                    }
                    Event.HideRequestAssistance -> scope.launch {
                        bottomSheetState.hide()
                    }
                    Event.ExitToAuthActivity -> {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
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
            }
        }
    }

}