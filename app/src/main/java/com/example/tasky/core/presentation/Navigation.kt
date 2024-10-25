package com.example.tasky.core.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaDetailScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaItemEditScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaScreen
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.onboarding.onboarding.presentation.ui.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.ui.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.LoginViewModel
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.RegisterViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val description = UiText.StringResource(R.string.description).asString()
    val title = UiText.StringResource(R.string.title).asString()

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

                    loginViewModel.checkUserSession()

                    val sessionState by loginViewModel.sessionState.collectAsState()

                    LaunchedEffect(sessionState) {
                        sessionState?.let { state: LoginViewModel.SessionState ->
                            when (state) {
                                is LoginViewModel.SessionState.Valid -> {
                                    navController.navigate(Screen.Agenda) {
                                        popUpTo(Screen.Login) { inclusive = true }
                                    }
                                }

                                // Do nothing
                                is LoginViewModel.SessionState.Invalid -> {}
                            }
                        }
                    }
                    if (sessionState is LoginViewModel.SessionState.Invalid) {
                        // Stay on LoginScreen and allow the user to log in manually
                        LoginScreen(
                            loginViewModel = loginViewModel,
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            },
                            onNavigateToAgenda = { navController.navigate(Screen.Agenda) }
                        )
                    }

                }
                composable<Screen.Agenda> {
                    val agendaViewModel = hiltViewModel<AgendaViewModel>()
                    AgendaScreen(
                        agendaViewModel = agendaViewModel,
                        onAgendaDetailPressed = { navController.navigate(Screen.AgendaDetail) },
                        onLogoutNavigateToLogin = {
                            navController.navigate(Screen.Login) {
                                popUpTo(Screen.Login) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
                composable<Screen.AgendaDetail> {
                    val agendaDetailViewModel = hiltViewModel<AgendaDetailViewModel>()
                    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

                    savedStateHandle?.get<String>(title)?.let { newTitle ->
                        agendaDetailViewModel.updateState(
                            AgendaDetailStateUpdate.UpdateTitle(
                                newTitle
                            )
                        )
                    }

                    savedStateHandle?.get<String>(description)?.let { newDescription ->
                        agendaDetailViewModel.updateState(
                            AgendaDetailStateUpdate.UpdateDescription(
                                newDescription
                            )
                        )
                    }

                    AgendaDetailScreen(
                        agendaDetailViewModel = agendaDetailViewModel,
                        onNavigateToAgendaScreen = {
                            navController.navigate(Screen.Agenda)
                        },
                        onClose = { navController.navigateUp() },
                        onEditPressed = {
                            navController.navigate(
                                Screen.AgendaItemEdit(
                                    title = agendaDetailViewModel.state.value.task.title,
                                    description = agendaDetailViewModel.state.value.task.description,
                                    agendaDetailViewModel.state.value.editType
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
                        description = args.description ?: "",
                        editType = args.editType,
                        onBackPressed = { navController.navigateUp() },
                        onSavePressed = {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                title,
                                agendaItemEditViewModel.state.value.title
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                description,
                                agendaItemEditViewModel.state.value.description
                            )
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}