package dev.rodrigo.levelupbunpo.ui

sealed class UiState {
    object LOADING : UiState()
    object SUCCESS : UiState()
    data class ERROR(val message: String) : UiState()

}