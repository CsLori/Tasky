package com.example.tasky.core.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasky.Screen
import com.example.tasky.onboarding.onboarding.presentation.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.RegisterViewModel

@Composable
fun Navigation() {
    val registerViewModel = hiltViewModel<RegisterViewModel>()
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Register,
            ) {
                composable<Screen.Register> {
                    RegisterScreen(registerViewModel = registerViewModel,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login) {
                                popUpTo(
                                    0
                                )
                            }
                        })
                }
                composable<Screen.Login> {
                    LoginScreen(onNavigateLoRegister = {
                        navController.navigate(Screen.Register) {
                            popUpTo(
                                0
                            )
                        }
                    })
                }
            }
        }
    }
}