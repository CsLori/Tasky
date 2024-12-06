package com.example.tasky.core.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.tasky.Constants.DESCRIPTION
import com.example.tasky.Constants.TITLE
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_presentation.ui.AgendaDetailScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaItemEditScreen
import com.example.tasky.agenda.agenda_presentation.ui.AgendaScreen
import com.example.tasky.agenda.agenda_presentation.ui.PhotoScreen
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.onboarding.onboarding.presentation.ui.login.LoginScreen
import com.example.tasky.onboarding.onboarding.presentation.ui.register.RegisterScreen
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.LoginViewModel

const val PHOTO_ID = "photoId"

@Composable
fun Navigation(userPrefsRepository: UserPrefsRepository) {
    val navController = rememberNavController()

    val authInfo by userPrefsRepository.authInfo.collectAsStateWithLifecycle()

    // Global authentication check
    LaunchedEffect(authInfo) {
        if (navController.currentBackStackEntry != null //To avoid crash
            && (authInfo == null || authInfo?.accessToken?.isEmpty() == true)) {
            navController.navigate(Screen.Login) {
                popUpTo(Screen.Login) { inclusive = true }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Login,
            ) {
                composable<Screen.Register> {
                    RegisterScreen(
                        navController = navController
                    )
                }
                composable<Screen.Login> {
                    val loginViewModel = hiltViewModel<LoginViewModel>()

                    loginViewModel.checkUserSession()

                    val sessionState by loginViewModel.sessionState.collectAsStateWithLifecycle()

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
                            navController = navController
                        )
                    }

                }
                composable<Screen.Agenda> {
                    AgendaScreen(navController = navController)
                }
                composable<Screen.AgendaDetail>(
                    deepLinks = listOf(
                        navDeepLink<Screen.AgendaDetail>(
                            basePath = "tasky://agenda_detail"
                        )
                    )
                ) {
                    val agendaDetailViewModel = hiltViewModel<AgendaDetailViewModel>()
                    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                    val args = it.toRoute<Screen.AgendaDetail>()

                    LaunchedEffect(savedStateHandle?.get<String>(PHOTO_ID)) {
                        savedStateHandle?.get<String>(PHOTO_ID)?.let { safePhotoId ->
                            agendaDetailViewModel.deletePhoto(safePhotoId)
                            savedStateHandle.remove<String>(PHOTO_ID)
                        }
                    }
                    val navigationTitle = agendaDetailViewModel.state.value.title

                    val navigationDescription = agendaDetailViewModel.state.value.description

                    savedStateHandle?.get<String>(TITLE)?.let { newTitle ->
                        agendaDetailViewModel.updateState(
                            AgendaDetailStateUpdate.UpdateTitle(
                                newTitle
                            )
                        )
                    }

                    savedStateHandle?.get<String>(DESCRIPTION)?.let { newDescription ->
                        agendaDetailViewModel.updateState(
                            AgendaDetailStateUpdate.UpdateDescription(
                                newDescription
                            )
                        )
                    }

                    AgendaDetailScreen(
                        agendaDetailViewModel = agendaDetailViewModel,
                        navigateToAgendaScreen = { navController.navigateUp() },
                        onEditPressed = {
                            navController.navigate(
                                Screen.AgendaItemEdit(
                                    title = navigationTitle,
                                    description = navigationDescription,
                                    editType = agendaDetailViewModel.state.value.editType
                                )
                            )
                        },
                        agendaItemId = args.agendaItemId,
                        navigateToPhotoScreen = { photoId ->
                            val photoUrl =
                                (agendaDetailViewModel.state.value.details as? AgendaItemDetails.Event)?.photos
                                    ?.firstOrNull { photo -> photo.key == photoId }?.url

                            if (photoUrl != null && photoId != null) {
                                navController.navigate(Screen.Photo(photoId, photoUrl))
                            }
                        }
                    )
                }
                composable<Screen.AgendaItemEdit> {
                    val args = it.toRoute<Screen.AgendaItemEdit>()
                    AgendaItemEditScreen(
                        title = args.title,
                        description = args.description ?: "",
                        editType = args.editType,
                        onBackPressed = { navController.navigateUp() },
                        onSavePressed = { newTitle, newDescription ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                TITLE,
                                newTitle
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                DESCRIPTION,
                                newDescription
                            )
                            navController.navigateUp()
                        }
                    )
                }

                composable<Screen.Photo> {
                    PhotoScreen(
                        navigateBack = { navController.navigateUp() },
                        deletePhoto = { photoId ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                PHOTO_ID,
                                photoId
                            )
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}