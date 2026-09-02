package com.example.opticloudtheempire.ui.home

import com.example.opticloudtheempire.data.model.InventoryAlert

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val alerts: List<InventoryAlert>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
