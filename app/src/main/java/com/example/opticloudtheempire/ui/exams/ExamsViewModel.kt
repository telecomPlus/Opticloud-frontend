package com.example.opticloudtheempire.ui.exams

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opticloudtheempire.data.model.*
import com.example.opticloudtheempire.data.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamsViewModel : ViewModel() {

    private val gson = Gson()

    // --- ESTADOS DE PANTALLAS ---
    private val _formState = MutableStateFlow<ExamFormUiState>(ExamFormUiState.Idle)
    val formState: StateFlow<ExamFormUiState> = _formState.asStateFlow()

    private val _historyState = MutableStateFlow<ExamHistoryUiState>(ExamHistoryUiState.Loading)
    val historyState: StateFlow<ExamHistoryUiState> = _historyState.asStateFlow()

    private val _detailState = MutableStateFlow<ExamDetailUiState>(ExamDetailUiState.Loading)
    val detailState: StateFlow<ExamDetailUiState> = _detailState.asStateFlow()

    // --- PACIENTES DISPONIBLES Y SELECCIONADO ---
    var availablePatients by mutableStateOf<List<Patient>>(emptyList())
        private set
    var isLoadingPatients by mutableStateOf(false)
        private set
    var selectedPatient by mutableStateOf<Patient?>(null)

    // --- CAMPOS GENERALES DEL FORMULARIO ---
    var examType by mutableStateOf("examen_visual")
    var reasonForVisit by mutableStateOf("")

    // --- GRADUACIÓN OJO DERECHO (OD) ---
    var odSphere by mutableStateOf("")
    var odCylinder by mutableStateOf("")
    var odAxis by mutableStateOf("")
    var odAddition by mutableStateOf("")
    var odPrism by mutableStateOf("")
    var odPrismBase by mutableStateOf("")
    var odVisualAcuityDistance by mutableStateOf("")
    var odVisualAcuityNear by mutableStateOf("")

    // --- GRADUACIÓN OJO IZQUIERDO (OI / OS) ---
    var osSphere by mutableStateOf("")
    var osCylinder by mutableStateOf("")
    var osAxis by mutableStateOf("")
    var osAddition by mutableStateOf("")
    var osPrism by mutableStateOf("")
    var osPrismBase by mutableStateOf("")
    var osVisualAcuityDistance by mutableStateOf("")
    var osVisualAcuityNear by mutableStateOf("")

    // --- MEDICIONES VISUALES (OPCIONALES) ---
    var pupillaryDistance by mutableStateOf("")
    var rightPupillaryDistance by mutableStateOf("")
    var leftPupillaryDistance by mutableStateOf("")
    var intraocularPressureOD by mutableStateOf("")
    var intraocularPressureOS by mutableStateOf("")
    var workingDistance by mutableStateOf("")

    // --- INFORMACIÓN CLÍNICA ---
    var diagnosis by mutableStateOf("")
    var recommendations by mutableStateOf("")
    var notes by mutableStateOf("")
    var followUpDate by mutableStateOf("")

    // Errores de validación específicos por campo
    var fieldErrors by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    init {
        loadAvailablePatients()
    }

    /**
     * Carga los pacientes existentes para el selector
     */
    fun loadAvailablePatients() {
        viewModelScope.launch {
            isLoadingPatients = true
            try {
                val patients = RetrofitClient.instance.getPatients()
                availablePatients = patients
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingPatients = false
            }
        }
    }

    /**
     * Preselecciona un paciente a partir de su ID si se viene desde el directorio
     */
    fun selectPatientById(patientId: String?) {
        if (patientId.isNullOrBlank()) return

        // Si ya está en la lista disponible
        val found = availablePatients.firstOrNull { it._id == patientId }
        if (found != null) {
            selectedPatient = found
        } else {
            // Intentar cargarlo individualmente si aún no estaba en lista
            viewModelScope.launch {
                try {
                    val patient = RetrofitClient.instance.getPatientById(patientId)
                    selectedPatient = patient
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Limpia o cambia el paciente seleccionado
     */
    fun onSelectPatient(patient: Patient?) {
        selectedPatient = patient
        if (fieldErrors.containsKey("patientId")) {
            fieldErrors = fieldErrors - "patientId"
        }
    }

    /**
     * Convierte texto con formato regional flexible (acepta ',' y '.') a Double opcional
     */
    private fun parseDecimal(value: String): Double? {
        val trimmed = value.trim().replace(',', '.')
        return trimmed.toDoubleOrNull()
    }

    /**
     * Convierte texto a entero opcional
     */
    private fun parseInt(value: String): Int? {
        return value.trim().toIntOrNull()
    }

    /**
     * Valida las reglas clínicas antes de enviar la petición
     */
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        // 1. Paciente obligatorio
        if (selectedPatient == null || selectedPatient?._id.isNullOrBlank()) {
            errors["patientId"] = "Debes seleccionar un paciente para asociar el examen."
        }

        // 2. Validación de OD
        val odCyl = parseDecimal(odCylinder)
        val odAx = parseInt(odAxis)

        if (odCyl != null && odCyl != 0.0) {
            if (odAxis.isBlank()) {
                errors["od.axis"] = "El eje es obligatorio cuando el cilindro es distinto de 0."
            } else if (odAx == null || odAx !in 1..180) {
                errors["od.axis"] = "El eje debe ser un número entero entre 1 y 180°."
            }
        } else if (odAxis.isNotBlank()) {
            if (odAx == null || odAx !in 1..180) {
                errors["od.axis"] = "Si se registra eje, debe estar entre 1 y 180°."
            }
        }

        // 3. Validación de OS (OI)
        val osCyl = parseDecimal(osCylinder)
        val osAx = parseInt(osAxis)

        if (osCyl != null && osCyl != 0.0) {
            if (osAxis.isBlank()) {
                errors["os.axis"] = "El eje es obligatorio cuando el cilindro es distinto de 0."
            } else if (osAx == null || osAx !in 1..180) {
                errors["os.axis"] = "El eje debe ser un número entero entre 1 y 180°."
            }
        } else if (osAxis.isNotBlank()) {
            if (osAx == null || osAx !in 1..180) {
                errors["os.axis"] = "Si se registra eje, debe estar entre 1 y 180°."
            }
        }

        fieldErrors = errors
        return errors.isEmpty()
    }

    /**
     * Registra el examen optométrico contra el backend
     */
    fun saveExam(onSuccessCallback: (ExamResponseDto) -> Unit = {}) {
        if (!validateForm()) {
            _formState.value = ExamFormUiState.Error(
                message = "Por favor corrige los campos señalados antes de guardar.",
                fieldErrors = fieldErrors
            )
            return
        }

        val patient = selectedPatient ?: return

        _formState.value = ExamFormUiState.Submitting

        viewModelScope.launch {
            try {
                // Construcción de OD
                val odDto = EyeGraduationDto(
                    sphere = parseDecimal(odSphere),
                    cylinder = parseDecimal(odCylinder),
                    axis = parseInt(odAxis),
                    addition = parseDecimal(odAddition),
                    prism = parseDecimal(odPrism),
                    prismBase = odPrismBase.trim().ifBlank { null },
                    visualAcuityDistance = odVisualAcuityDistance.trim().ifBlank { null },
                    visualAcuityNear = odVisualAcuityNear.trim().ifBlank { null }
                )

                // Construcción de OS
                val osDto = EyeGraduationDto(
                    sphere = parseDecimal(osSphere),
                    cylinder = parseDecimal(osCylinder),
                    axis = parseInt(osAxis),
                    addition = parseDecimal(osAddition),
                    prism = parseDecimal(osPrism),
                    prismBase = osPrismBase.trim().ifBlank { null },
                    visualAcuityDistance = osVisualAcuityDistance.trim().ifBlank { null },
                    visualAcuityNear = osVisualAcuityNear.trim().ifBlank { null }
                )

                // Mediciones visuales (solo se agregan si tienen datos)
                val pd = parseDecimal(pupillaryDistance)
                val rpd = parseDecimal(rightPupillaryDistance)
                val lpd = parseDecimal(leftPupillaryDistance)
                val iopOd = parseDecimal(intraocularPressureOD)
                val iopOs = parseDecimal(intraocularPressureOS)
                val wd = parseDecimal(workingDistance)

                val visualMeasurements = if (pd != null || rpd != null || lpd != null || iopOd != null || iopOs != null || wd != null) {
                    VisualMeasurementsDto(
                        pupillaryDistance = pd,
                        rightPupillaryDistance = rpd,
                        leftPupillaryDistance = lpd,
                        intraocularPressureOD = iopOd,
                        intraocularPressureOS = iopOs,
                        workingDistance = wd
                    )
                } else null

                val requestDto = ExamRequestDto(
                    patientId = patient._id ?: "",
                    examType = examType,
                    reasonForVisit = reasonForVisit.trim().ifBlank { null },
                    od = odDto,
                    os = osDto,
                    visualMeasurements = visualMeasurements,
                    diagnosis = diagnosis.trim().ifBlank { null },
                    recommendations = recommendations.trim().ifBlank { null },
                    notes = notes.trim().ifBlank { null },
                    followUpDate = followUpDate.trim().ifBlank { null }
                )

                val response = RetrofitClient.instance.registerExam(requestDto)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val examData = apiResponse.data

                    if (examData != null) {
                        _formState.value = ExamFormUiState.Success(
                            exam = examData,
                            message = apiResponse.message ?: "Examen registrado exitosamente."
                        )
                        onSuccessCallback(examData)
                    } else {
                        _formState.value = ExamFormUiState.Error(
                            message = apiResponse.message ?: "Respuesta inesperada del servidor"
                        )
                    }
                } else {
                    // Procesar cuerpo de error HTTP del backend (400, 401, 403, 500)
                    val errorBody = response.errorBody()?.string()
                    val (errorMessage, backendFieldErrors) = parseBackendError(errorBody, response.code())
                    fieldErrors = backendFieldErrors
                    _formState.value = ExamFormUiState.Error(
                        message = errorMessage,
                        fieldErrors = backendFieldErrors
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _formState.value = ExamFormUiState.Error(
                    message = "Error de conexión con el servidor: ${e.localizedMessage ?: "Verifica que el backend esté activo."}"
                )
            }
        }
    }

    /**
     * Limpia el formulario para un nuevo registro
     */
    fun resetForm() {
        _formState.value = ExamFormUiState.Idle
        examType = "examen_visual"
        reasonForVisit = ""
        odSphere = ""
        odCylinder = ""
        odAxis = ""
        odAddition = ""
        odPrism = ""
        odPrismBase = ""
        odVisualAcuityDistance = ""
        odVisualAcuityNear = ""

        osSphere = ""
        osCylinder = ""
        osAxis = ""
        osAddition = ""
        osPrism = ""
        osPrismBase = ""
        osVisualAcuityDistance = ""
        osVisualAcuityNear = ""

        pupillaryDistance = ""
        rightPupillaryDistance = ""
        leftPupillaryDistance = ""
        intraocularPressureOD = ""
        intraocularPressureOS = ""
        workingDistance = ""

        diagnosis = ""
        recommendations = ""
        notes = ""
        followUpDate = ""
        fieldErrors = emptyMap()
    }

    /**
     * Consulta el historial clínico de un paciente
     */
    fun fetchPatientExams(patientId: String, page: Int = 1) {
        viewModelScope.launch {
            _historyState.value = ExamHistoryUiState.Loading
            try {
                val response = RetrofitClient.instance.getPatientExamsHistory(patientId, page = page, limit = 10)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val rawData = apiResponse.data
                    val examsList = parseExamsList(rawData)

                    if (examsList.isEmpty()) {
                        _historyState.value = ExamHistoryUiState.Empty
                    } else {
                        val pagination = parsePagination(rawData)
                        _historyState.value = ExamHistoryUiState.Success(
                            exams = examsList,
                            pagination = pagination
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val (errorMsg, _) = parseBackendError(errorBody, response.code())
                    _historyState.value = ExamHistoryUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _historyState.value = ExamHistoryUiState.Error(
                    "Error al cargar el historial: ${e.localizedMessage ?: "Verifica la conexión"}"
                )
            }
        }
    }

    /**
     * Consulta el detalle individual de un examen
     */
    fun fetchExamDetail(examId: String) {
        viewModelScope.launch {
            _detailState.value = ExamDetailUiState.Loading
            try {
                val response = RetrofitClient.instance.getExamById(examId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val examData = apiResponse.data

                    if (examData != null) {
                        _detailState.value = ExamDetailUiState.Success(examData)
                    } else {
                        _detailState.value = ExamDetailUiState.Error(
                            apiResponse.message ?: "No se encontraron datos del examen"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val (errorMsg, _) = parseBackendError(errorBody, response.code())
                    _detailState.value = ExamDetailUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _detailState.value = ExamDetailUiState.Error(
                    "Error al cargar el detalle: ${e.localizedMessage ?: "Verifica la conexión"}"
                )
            }
        }
    }

    /**
     * Extrae de forma tolerante la lista de exámenes de un JsonElement (sea lista directa u objeto paginado)
     */
    private fun parseExamsList(element: JsonElement?): List<ExamResponseDto> {
        if (element == null || element.isJsonNull) return emptyList()
        return try {
            val listType = object : TypeToken<List<ExamResponseDto>>() {}.type
            if (element.isJsonArray) {
                gson.fromJson<List<ExamResponseDto>>(element, listType) ?: emptyList()
            } else if (element.isJsonObject) {
                val obj = element.asJsonObject
                when {
                    obj.has("exams") && obj.get("exams").isJsonArray -> {
                        gson.fromJson<List<ExamResponseDto>>(obj.get("exams"), listType) ?: emptyList()
                    }
                    obj.has("docs") && obj.get("docs").isJsonArray -> {
                        gson.fromJson<List<ExamResponseDto>>(obj.get("docs"), listType) ?: emptyList()
                    }
                    obj.has("data") && obj.get("data").isJsonArray -> {
                        gson.fromJson<List<ExamResponseDto>>(obj.get("data"), listType) ?: emptyList()
                    }
                    else -> emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Extrae metadata de paginación si viene presente
     */
    private fun parsePagination(element: JsonElement?): PaginationDto? {
        if (element == null || !element.isJsonObject) return null
        val obj = element.asJsonObject
        return try {
            when {
                obj.has("pagination") && obj.get("pagination").isJsonObject -> {
                    gson.fromJson(obj.get("pagination"), PaginationDto::class.java)
                }
                obj.has("total") || obj.has("totalPages") -> {
                    gson.fromJson(obj, PaginationDto::class.java)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extrae mensajes legibles de las respuestas de error del backend
     */
    private fun parseBackendError(errorBodyString: String?, statusCode: Int): Pair<String, Map<String, String>> {
        val defaultMsg = when (statusCode) {
            401 -> "Sesión expirada o no autorizada. Por favor vuelve a iniciar sesión."
            403 -> "No tienes permisos de optometrista para registrar exámenes."
            404 -> "Recurso no encontrado en el servidor."
            500 -> "Error interno del servidor. Por favor intenta más tarde."
            else -> "Error en la solicitud ($statusCode)."
        }

        if (errorBodyString.isNullOrBlank()) {
            return Pair(defaultMsg, emptyMap())
        }

        return try {
            val errorResponse = gson.fromJson(errorBodyString, ApiResponse::class.java)
            val fieldMap = mutableMapOf<String, String>()
            val errorLines = mutableListOf<String>()

            if (!errorResponse.errors.isNullOrEmpty()) {
                errorResponse.errors.forEach { err ->
                    val path = err.path ?: err.param ?: ""
                    val msg = err.msg ?: "Valor inválido"
                    if (path.isNotBlank()) {
                        fieldMap[path] = msg
                    }
                    errorLines.add("• $msg")
                }
            }

            val finalMessage = when {
                errorLines.isNotEmpty() -> errorLines.joinToString("\n")
                !errorResponse.message.isNullOrBlank() -> errorResponse.message
                else -> defaultMsg
            }

            Pair(finalMessage, fieldMap)
        } catch (e: Exception) {
            Pair(defaultMsg, emptyMap())
        }
    }
}
