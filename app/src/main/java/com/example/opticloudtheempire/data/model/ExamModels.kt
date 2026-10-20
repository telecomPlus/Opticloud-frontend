package com.example.opticloudtheempire.data.model

import com.google.gson.annotations.SerializedName

/**
 * Envoltorio común para respuestas de la API de OptiCloud
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("errors") val errors: List<ApiValidationError>? = null
) {
    fun getDisplayErrorMessage(): String {
        if (!errors.isNullOrEmpty()) {
            return errors.mapNotNull { it.msg }.filter { it.isNotBlank() }.joinToString("\n")
                .ifBlank { message ?: "Error de validación en la solicitud" }
        }
        return message ?: "Error al procesar la solicitud"
    }
}

/**
 * Detalle de error de validación retornado por el backend
 */
data class ApiValidationError(
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("path") val path: String? = null,
    @SerializedName("param") val param: String? = null,
    @SerializedName("value") val value: Any? = null
)

/**
 * Graduación óptica para un ojo (OD u OI)
 */
data class EyeGraduationDto(
    @SerializedName("sphere") val sphere: Double? = null,
    @SerializedName("cylinder") val cylinder: Double? = null,
    @SerializedName("axis") val axis: Int? = null,
    @SerializedName("addition") val addition: Double? = null,
    @SerializedName("prism") val prism: Double? = null,
    @SerializedName("prismBase") val prismBase: String? = null,
    @SerializedName("visualAcuityDistance") val visualAcuityDistance: String? = null,
    @SerializedName("visualAcuityNear") val visualAcuityNear: String? = null
) {
    /**
     * Resumen legible de la graduación (ej: Esf: -2.50 Cil: -0.75 x 180°)
     */
    fun toSummaryString(): String {
        val parts = mutableListOf<String>()
        if (sphere != null) {
            val sign = if (sphere > 0) "+" else ""
            parts.add("Esf: $sign${String.format("%.2f", sphere)}")
        }
        if (cylinder != null && cylinder != 0.0) {
            val sign = if (cylinder > 0) "+" else ""
            val axisText = if (axis != null) " x ${axis}°" else ""
            parts.add("Cil: $sign${String.format("%.2f", cylinder)}$axisText")
        }
        if (addition != null && addition != 0.0) {
            val sign = if (addition > 0) "+" else ""
            parts.add("Add: $sign${String.format("%.2f", addition)}")
        }
        if (!visualAcuityDistance.isNullOrBlank()) {
            parts.add("AV: $visualAcuityDistance")
        }
        return if (parts.isEmpty()) "Sin graduación registrada" else parts.joinToString("  ")
    }
}

/**
 * Mediciones visuales complementarias
 */
data class VisualMeasurementsDto(
    @SerializedName("pupillaryDistance") val pupillaryDistance: Double? = null,
    @SerializedName("rightPupillaryDistance") val rightPupillaryDistance: Double? = null,
    @SerializedName("leftPupillaryDistance") val leftPupillaryDistance: Double? = null,
    @SerializedName("intraocularPressureOD") val intraocularPressureOD: Double? = null,
    @SerializedName("intraocularPressureOS") val intraocularPressureOS: Double? = null,
    @SerializedName("workingDistance") val workingDistance: Double? = null
)

/**
 * Petición para registrar un nuevo examen optométrico
 */
data class ExamRequestDto(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("examType") val examType: String = "examen_visual",
    @SerializedName("reasonForVisit") val reasonForVisit: String? = null,
    @SerializedName("od") val od: EyeGraduationDto,
    @SerializedName("os") val os: EyeGraduationDto,
    @SerializedName("visualMeasurements") val visualMeasurements: VisualMeasurementsDto? = null,
    @SerializedName("diagnosis") val diagnosis: String? = null,
    @SerializedName("recommendations") val recommendations: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("followUpDate") val followUpDate: String? = null
)

/**
 * Modelo completo del examen devuelto por el backend
 */
data class ExamResponseDto(
    @SerializedName("_id") val _id: String,
    @SerializedName("patientId") val patientId: Any? = null,
    @SerializedName("examDate") val examDate: String? = null,
    @SerializedName("examType") val examType: String? = null,
    @SerializedName("optometristId") val optometristId: Any? = null,
    @SerializedName("reasonForVisit") val reasonForVisit: String? = null,
    @SerializedName("od") val od: EyeGraduationDto? = null,
    @SerializedName("os") val os: EyeGraduationDto? = null,
    @SerializedName("visualMeasurements") val visualMeasurements: VisualMeasurementsDto? = null,
    @SerializedName("diagnosis") val diagnosis: String? = null,
    @SerializedName("recommendations") val recommendations: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("followUpDate") val followUpDate: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    fun getFormattedExamType(): String {
        return when (examType) {
            "examen_visual" -> "Examen visual"
            "control" -> "Control"
            "lentes_contacto" -> "Lentes de contacto"
            "urgencia" -> "Urgencia"
            "refraccion" -> "Refracción"
            else -> examType?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Examen General"
        }
    }

    fun getPatientName(): String {
        if (patientId is Map<*, *>) {
            return patientId["name"]?.toString() ?: "Paciente"
        }
        return "Paciente"
    }

    fun getPatientDocument(): String? {
        if (patientId is Map<*, *>) {
            return patientId["documentNumber"]?.toString()
        }
        return null
    }

    fun getOptometristName(): String {
        if (optometristId is Map<*, *>) {
            return optometristId["name"]?.toString() ?: "Optometrista"
        }
        return "Optometrista"
    }
}

/**
 * Metadata de paginación para el historial de exámenes
 */
data class PaginationDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("totalPages") val totalPages: Int? = null
)
