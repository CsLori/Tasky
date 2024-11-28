package com.example.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.core.presentation.Navigation
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPrefsRepository: UserPrefsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.init()
        installSplashScreen()
        enableEdgeToEdge()
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false

        setContent {
            AppTheme {
                window.statusBarColor = AppTheme.colors.black.toArgb()
                window.navigationBarColor = AppTheme.colors.white.toArgb()
                Navigation(userPrefsRepository)
            }
        }
    }
}