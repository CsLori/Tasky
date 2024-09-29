package com.example.tasky.onboarding.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.onboarding.onboarding_data.repository.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.None)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState("", "", ""))
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        _state.update { RegisterState.Loading }
        try {
            viewModelScope.launch {
                userRepositoryImpl.register(name, email, password)
                _state.update { RegisterState.Success }
            }

        } catch (e: Exception) {
            _state.update { RegisterState.Error("Something went wrong!") }
        }
    }

    data class RegisterUiState(
        val name: String,
        val email: String,
        val password: String
    )

    sealed class RegisterState {
        data object None : RegisterState()
        data object Loading : RegisterState()
        data object Success : RegisterState()
        data class Error(val error: String?) : RegisterState()
    }

}