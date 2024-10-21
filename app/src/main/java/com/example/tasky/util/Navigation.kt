package com.example.tasky.util

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.navigation.toRoute
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaDetailScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaItemEditScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaScreen
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.onboarding.onboarding.presentation.ui.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.ui.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.LoginViewModel
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.RegisterViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Login,
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
                        onAgendaDetailPressed = { navController.navigate(Screen.AgendaDetail) },
                    )
                }
                composable<Screen.AgendaDetail> {
                    val agendaDetailViewModel = hiltViewModel<AgendaDetailViewModel>()
                    AgendaDetailScreen(
                        agendaDetailViewModel = agendaDetailViewModel,
                        onNavigateToAgendaScreen = {
                            navController.navigate(Screen.Agenda)
                        },
                        onClose = { navController.popBackStack() },
                        onEditPressed = {
                            navController.navigate(
                                Screen.AgendaItemEdit(
                                    title = agendaDetailViewModel.state.value.task.title ?: "This",
                                    description = agendaDetailViewModel.state.value.task.title
                                )
                            )
                        }
                    )
                }
                composable<Screen.AgendaItemEdit> {
                    val args = it.toRoute<Screen.AgendaItemEdit>()
                    val agendaItemEditViewModel = hiltViewModel<AgendaItemEditViewModel>()
                    AgendaItemEditScreen(
                        agendaItemEditViewModel = agendaItemEditViewModel,
                        title = args.title,
                        description = args.description ?: ""
                    )
                }
            }
        }
    }
}