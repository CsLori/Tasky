package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val photoUrl = savedStateHandle.get<String>("photoUrl")
    val photoKey = savedStateHandle.get<String>("photoKey")

}

sealed interface PhotoAction {
    data object OnNavigateBack : PhotoAction
    data class OnDeletePhoto(val photoId: String) : PhotoAction
}