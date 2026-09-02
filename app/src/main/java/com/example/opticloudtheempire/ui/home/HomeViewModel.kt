package com.example.opticloudtheempire.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opticloudtheempire.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Consumir el endpoint de alertas
                val alerts = RetrofitClient.instance.getInventoryAlerts()
                _uiState.value = HomeUiState.Success(alerts)
            } catch (e: Exception) {
                // Manejo de error. En caso de que el backend no tenga el endpoint implementado aún,
                // se puede mostrar el error real.
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido de red")
            }
        }
    }
}
