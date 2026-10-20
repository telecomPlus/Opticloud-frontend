package com.example.opticloudtheempire.data.network

import com.example.opticloudtheempire.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // --- ALERTAS, PACIENTES Y EXÁMENES ---
    @GET("dashboard/alerts")
    suspend fun getInventoryAlerts(): List<InventoryAlert>

    // --- EXÁMENES OPTOMÉTRICOS ---
    @POST("exams")
    suspend fun registerExam(
        @Body exam: ExamRequestDto
    ): Response<ApiResponse<ExamResponseDto>>

    @GET("exams/{id}")
    suspend fun getExamById(
        @Path("id") examId: String
    ): Response<ApiResponse<ExamResponseDto>>

    @GET("patients/{patientId}/exams")
    suspend fun getPatientExamsHistory(
        @Path("patientId") patientId: String,
        @retrofit2.http.Query("page") page: Int = 1,
        @retrofit2.http.Query("limit") limit: Int = 10
    ): Response<ApiResponse<com.google.gson.JsonElement>>

    @GET("exams")
    suspend fun getAllExams(
        @retrofit2.http.Query("patientId") patientId: String? = null,
        @retrofit2.http.Query("examType") examType: String? = null,
        @retrofit2.http.Query("page") page: Int = 1,
        @retrofit2.http.Query("limit") limit: Int = 20
    ): Response<ApiResponse<com.google.gson.JsonElement>>

    @GET("patients/{id}/exams")
    suspend fun getPatientExams(@Path("id") patientId: String): List<Exam>

    @POST("exams")
    suspend fun createExam(@Body exam: Exam): Exam

    @GET("patients")
    suspend fun getPatients(): List<Patient>

    @GET("patients/{id}")
    suspend fun getPatientById(@Path("id") patientId: String): Patient

    @POST("patients")
    suspend fun createPatient(@Body patient: Patient): Patient

    @PUT("patients/{id}")
    suspend fun updatePatient(@Path("id") patientId: String, @Body patient: Patient): Patient

    @DELETE("patients/{id}")
    suspend fun deletePatient(@Path("id") patientId: String): Response<Unit>
}