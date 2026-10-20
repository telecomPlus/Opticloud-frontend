package com.example.opticloudtheempire.ui.exams

import com.example.opticloudtheempire.data.model.ExamResponseDto
import com.example.opticloudtheempire.data.model.PaginationDto

/**
 * Estados del formulario de registro de examen optométrico
 */
sealed interface ExamFormUiState {
    object Idle : ExamFormUiState
    object Submitting : ExamFormUiState
    data class Success(val exam: ExamResponseDto, val message: String) : ExamFormUiState
    data class Error(val message: String, val fieldErrors: Map<String, String> = emptyMap()) : ExamFormUiState
}

/**
 * Estados de la pantalla de historial de exámenes de un paciente
 */
sealed interface ExamHistoryUiState {
    object Loading : ExamHistoryUiState
    data class Success(
        val exams: List<ExamResponseDto>,
        val pagination: PaginationDto? = null
    ) : ExamHistoryUiState
    object Empty : ExamHistoryUiState
    data class Error(val message: String) : ExamHistoryUiState
}

/**
 * Estados de la pantalla de detalle de un examen optométrico
 */
sealed interface ExamDetailUiState {
    object Loading : ExamDetailUiState
    data class Success(val exam: ExamResponseDto) : ExamDetailUiState
    data class Error(val message: String) : ExamDetailUiState
}
