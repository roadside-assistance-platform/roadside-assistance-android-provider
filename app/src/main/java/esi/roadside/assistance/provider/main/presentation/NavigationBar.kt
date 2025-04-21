package esi.roadside.assistance.provider.main.presentation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import esi.roadside.assistance.provider.core.presentation.util.navigationBarItemColors

@Composable
fun NavigationBar(
    navController: NavHostController,
    route: Routes,
    modifier: Modifier = Modifier,
    badge: @Composable (BoxScope.(Routes) -> Unit) = {}
) {
    NavigationBar(modifier) {
        Routes.entries.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = screen == route,
                alwaysShowLabel = false,
                icon = {
                    BadgedBox({
                        badge(screen)
                    }) {
                        Icon(screen.icon, null)
                    }
                },
                colors = navigationBarItemColors(),
                label = {
                    Text(
                        stringResource(screen.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}