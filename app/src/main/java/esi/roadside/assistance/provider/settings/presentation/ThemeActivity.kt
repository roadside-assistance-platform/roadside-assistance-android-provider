package esi.roadside.assistance.provider.settings.presentation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.twotone.DarkMode
import androidx.compose.material.icons.twotone.InvertColors
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material.icons.twotone.SettingsSuggest
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.components.Dialog
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import esi.roadside.assistance.provider.settings.data.colors
import esi.roadside.assistance.provider.settings.data.themeOptions
import kotlin.getValue

class ThemeActivity : ComponentActivity() {
    val dataStore by inject<SettingsDataStore>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SetSystemBarColors(dataStore = dataStore)
            val listState = rememberLazyListState()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val context = LocalContext.current
            val isDark by dataStore.isDark().collectAsState(initial = false)
            val colorTheme by dataStore.colorTheme.collectAsState(initial = "green")
            var colorThemeDialogShown by remember { mutableStateOf(false) }
            val dynamicColorsChecked by dataStore.dynamicColors.collectAsState(initial = true)
            val extraDarkChecked by dataStore.extraDark.collectAsState(initial = true)
            val theme by dataStore.theme.collectAsState(initial = "system")
            val scope = rememberCoroutineScope()
            AppTheme {
                Scaffold(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(
                                    stringResource(id = R.string.theme),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { (context as Activity).finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        contentPadding = paddingValues,
                    ) {
                        settingsItem(
                            Icons.TwoTone.InvertColors,
                            R.string.app_theme,
                            R.string.choose_app_theme,
                        )
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                SingleChoiceSegmentedButtonRow {
                                    themeOptions.forEachIndexed { index, pair ->
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
                                            onClick = {
                                                scope.launch {
                                                    dataStore.saveSettings(theme = pair.first)
                                                }
                                            },
                                            selected = theme == pair.first,
                                        ) {
                                            Text(stringResource(id = pair.second))
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(4.dp))
                        }
                        checkSettingsItem(
                            icon = Icons.TwoTone.DarkMode,
                            title = R.string.extra_dark_colors,
                            text = R.string.extra_dark_description,
                            checked = extraDarkChecked,
                            visible = isDark,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    dataStore.saveSettings(extraDark = checked)
                                }
                            },
                        )
                        checkSettingsItem(
                            icon = Icons.TwoTone.SettingsSuggest,
                            title = R.string.dynamic_colors,
                            text = R.string.follow_system_dynamic_colors,
                            checked = dynamicColorsChecked,
                            visible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    dataStore.saveSettings(dynamic = checked)
                                }
                            },
                        )
                        settingsItem(
                            Icons.TwoTone.Palette,
                            R.string.color_palette,
                            colors.toMap()[colorTheme]!!,
                            onClick = { colorThemeDialogShown = true },
                            visible = (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) or !dynamicColorsChecked,
                        )
                    }
                }
                Dialog(
                    visible = colorThemeDialogShown,
                    onDismissRequest = { colorThemeDialogShown = false },
                    title = stringResource(R.string.color_palette),
                    centerTitle = true,
                    okListener = { colorThemeDialogShown = false },
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        settingsRadioItems(
                            colors,
                            colors.map { it.first }.indexOf(colorTheme),
                            { scope.launch { dataStore.saveSettings(colorTheme = colors.map { it.first }[it]) } },
                        ) { Text(stringResource(it.second)) }
                    }
                }
            }
        }
    }
}
