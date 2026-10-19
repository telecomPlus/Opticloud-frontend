package com.example.opticloudtheempire.ui.patients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opticloudtheempire.data.model.Patient
import com.example.opticloudtheempire.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PatientsListState {
    object Loading : PatientsListState
    data class Success(val patients: List<Patient>) : PatientsListState
    data class Error(val message: String) : PatientsListState
}

class PatientsViewModel : ViewModel() {

    // Formulario Nuevo Paciente
    var documentNumber by mutableStateOf("")
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")

    var isSaving by mutableStateOf(false)
    var saveErrorMessage by mutableStateOf<String?>(null)

    // Búsqueda en el directorio
    var searchQuery by mutableStateOf("")
        private set

    private val _listState = MutableStateFlow<PatientsListState>(PatientsListState.Loading)
    val listState: StateFlow<PatientsListState> = _listState.asStateFlow()

    init {
        fetchPatients()
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun fetchPatients() {
        viewModelScope.launch {
            _listState.value = PatientsListState.Loading
            try {
                val result = RetrofitClient.instance.getPatients()
                _listState.value = PatientsListState.Success(result)
            } catch (e: Exception) {
                _listState.value = PatientsListState.Error(e.message ?: "Error al cargar la lista")
            }
        }
    }


    fun updatePatient(patientId: String, patient: Patient) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updatePatient(patientId, patient)
                fetchPatients() // Recarga la lista para mostrar la información actualizada
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar error de actualización
            }
        }
    }

    fun savePatient(onSuccess: () -> Unit) {
        if (documentNumber.isBlank() || fullName.isBlank() || email.isBlank() || phone.isBlank()) {
            saveErrorMessage = "Por favor completa todos los campos requeridos (*)"
            return
        }
        viewModelScope.launch {
            isSaving = true
            saveErrorMessage = null
            try {
                val newPatient = Patient(
                    _id = null,
                    documentNumber = documentNumber.trim(),
                    name = fullName.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    address = address.trim().ifEmpty { null }
                )
                RetrofitClient.instance.createPatient(newPatient)

                // Limpiar formulario tras éxito
                documentNumber = ""
                fullName = ""
                email = ""
                phone = ""
                address = ""

                fetchPatients()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                saveErrorMessage = e.localizedMessage ?: "Error al guardar el paciente"
            } finally {
                isSaving = false
            }
        }
    }

    fun deletePatient(patientId: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deletePatient(patientId)

                // Recargar la lista después de eliminar
                fetchPatients()

            } catch (e: Exception) {
                e.printStackTrace()
                // Aquí puedes manejar el error de eliminación
            }
        }
    }
}