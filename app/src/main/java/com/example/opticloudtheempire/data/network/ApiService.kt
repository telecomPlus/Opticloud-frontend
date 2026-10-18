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