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
import com.example.tasky.agenda.agenda_presentation.ui.AgendaScreen
import com.example.tasky.agenda.agenda_presentation.ui.EventScreen
import com.example.tasky.agenda.agenda_presentation.ui.ReminderScreen
import com.example.tasky.agenda.agenda_presentation.ui.TaskScreen
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.onboarding.onboarding.presentation.ui.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.ui.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.LoginViewModel
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.RegisterViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Agenda,
            ) {
                composable<Screen.Register> {
                    val registerViewModel = hiltViewModel<RegisterViewModel>()
                    RegisterScreen(registerViewModel = registerViewModel,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login) {
                                popUpTo(Screen.Register) { inclusive = true }
                            }
                        })
                }
                composable<Screen.Login> {
                    val loginViewModel = hiltViewModel<LoginViewModel>()
                    LoginScreen(loginViewModel = loginViewModel, onNavigateToRegister = {
                        navController.navigate(Screen.Register) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }, onNavigateToAgenda = { navController.navigate(Screen.Agenda) })
                }
                composable<Screen.Agenda> {
                    val agendaViewModel = hiltViewModel<AgendaViewModel>()
                    AgendaScreen(
                        agendaViewModel = agendaViewModel,
                        onTaskPressed = { navController.navigate(Screen.Task) },
                        onEventPressed = { navController.navigate(Screen.Event) },
                        onReminderPressed = { navController.navigate(Screen.Reminder) })
                }
                composable<Screen.Task> {
                    TaskScreen()
                }
                composable<Screen.Reminder> {
                    ReminderScreen()
                }
                composable<Screen.Event> {
                    EventScreen()
                }
            }
        }
    }
}