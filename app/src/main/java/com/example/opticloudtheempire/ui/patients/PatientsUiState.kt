package com.example.opticloudtheempire.ui.patients

sealed interface PatientsUiState {
    object Idle : PatientsUiState
    object Loading : PatientsUiState
    object Success : PatientsUiState
    data class Error(val message: String) : PatientsUiState
}