package com.example.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasky.onboarding.onboarding.presentation.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.RegisterViewModel
import com.example.tasky.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val registerViewModel = hiltViewModel<RegisterViewModel>()
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Register,
                        ) {
                            composable<Screen.Register> {
                                RegisterScreen(registerViewModel, navController)
                            }
                            composable<Screen.Login> {
                                LoginScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}